package cn.jsuacm.ccw.service.projects.impl;

import cn.jsuacm.ccw.mapper.projects.ProjectMapper;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.pojo.projects.Collaborators;
import cn.jsuacm.ccw.pojo.projects.Project;
import cn.jsuacm.ccw.service.projects.CollaboratorsService;
import cn.jsuacm.ccw.service.projects.CommitService;
import cn.jsuacm.ccw.service.projects.ProjectService;
import cn.jsuacm.ccw.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * @ClassName ProjectServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 19:56
 */

@Service
@CacheConfig(cacheNames = "project")
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;


    @Autowired
    private CollaboratorsService collaboratorsService;



    @Autowired
    private CommitService commitService;



    @Autowired
    private RedisUtil redisUtil;


    /**
     * 定时任务
     */
    private static ScheduledThreadPoolExecutor executorService;

    private static ConcurrentHashMap<Integer, ScheduledFuture> timerMap = new ConcurrentHashMap<>();

    private static ScheduledExecutorService getExecutorService(){
        if (executorService == null){
            synchronized (ProjectServiceImpl.class){
                if (executorService == null){
                    executorService = new ScheduledThreadPoolExecutor(10,
                            new BasicThreadFactory.Builder().namingPattern("project-schedule-pool-%d").daemon(true).build());
                    executorService.setRemoveOnCancelPolicy(true);
                }
            }
        }
        return executorService;
    }


    /**
     * 添加一个定时任务以及缓存
     * @param pid 项目的id
     */
    private void addTimer(int pid){
        try {
            redisUtil.hmset(pid+"", new HashMap<Object, Object>());
            ScheduledFuture<?> scheduledFuture = getExecutorService().scheduleAtFixedRate(
                    new ProjectInfo(pid),
                    0,
                    18,
                    TimeUnit.HOURS);
            timerMap.put(pid, scheduledFuture);
        }catch (RejectedExecutionException e){
            throw new RuntimeException("无法添加项目到定时任务");
        }

    }


    /**
     * 删除一个定时任务以及缓存中的信息
     * @param pid
     */
    private void deleteTimer(int pid){
        ScheduledFuture scheduledFuture = timerMap.get(pid);
        if (scheduledFuture == null){
            return;
        }
        scheduledFuture.cancel(true);
        redisUtil.del(pid+"");

    }



    /**
     * 分页获取所有项目信息
     *
     * @param current  当前页
     * @param pageSize 页面大小
     * @return
     */
    @Override
    @Cacheable
    public PageResult<Project> getPages(int status, int current, int pageSize) {
        IPage<Project> iPage = getIPage(current, pageSize);
        IPage<Project> projectIPage = null;
        if (status == 0){
            projectIPage = projectMapper.selectPage(iPage, null);
        }else {
            QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", status);
            projectIPage = projectMapper.selectPage(iPage, queryWrapper);
        }
        PageResult<Project> pageResult = new PageResult<>();
        pageResult.setRow(projectIPage.getCurrent());
        pageResult.setPageSize(projectIPage.getSize());
        pageResult.setTatolSize(projectIPage.getTotal());
        pageResult.setPageContext(projectIPage.getRecords());
        return pageResult;
    }

    /**
     * 添加一个项目
     *
     * @param project
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult addProject(Project project) {
        project.setStatus(Project.PROJECT_OPEN);
        project.setDate(new Date());
        boolean save = save(project);
        if (save){
            addTimer(project.getId());
            return new MessageResult(save, "添加成功");
        }else {
            return new MessageResult(save, "添加失败");
        }
    }

    /**
     * 修改一个项目的信息, 不可以修改状态
     *
     * @param project
     * @return
     */
    @Override
    public MessageResult updateProject(Project project) {
        UpdateWrapper<Project> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", project.getId());

        if (project.getDescription() != null && project.getDescription().length() != 0){
            updateWrapper.set("description", project.getDescription());
        }

        if (project.getHtmlDescription() != null && project.getHtmlDescription().length() != 0){
            updateWrapper.set("html_description", project.getHtmlDescription());
        }

        if (project.getName() != null && project.getName().length() != 0){
            updateWrapper.set("name", project.getName());
        }
        boolean update = update(updateWrapper);
        if (update){
            return new MessageResult(update, "修改成功");
        }else {
            return new MessageResult(update, "修改失败");
        }
    }

    /**
     * 修改项目状态
     *
     * @param id  项目id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateStatus(int id) {
        Project project = getById(id);
        if (project == null){
            return new MessageResult(false, "没有这个项目");
        }
        project.setStatus(project.getStatus() * -1);
        UpdateWrapper<Project> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status",project.getStatus());
        boolean update = update(updateWrapper);
        if (update){
            if (project.getStatus() == Project.PROJECT_OPEN){
                addTimer(id);
            }else {
                deleteTimer(id);
            }
            return new MessageResult(update, "修改成功");
        }else {
            return new MessageResult(update, "修改失败");
        }
    }

    /**
     * 通过项目id删除项目
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult deleteById(int id) {
        // 确认有这个项目
        Project project = getById(id);
        if (project == null){
            return new MessageResult(false, "没有这个项目");
        }
        // 如果是开启状态就要取消对应的定时任务， 然后删除缓存
        if (project.getStatus() == Project.PROJECT_OPEN){
            updateStatus(id);
        }

        // 获取这个项目的人员
        List<Collaborators> collaborators = collaboratorsService.getByPid(project.getId());
        // 循环删除人员列表和这个人提交的提交记录
        for (Collaborators collaborator : collaborators){
            collaboratorsService.deleteById(collaborator.getId());
        }
        // 删除这个项目
        removeById(id);
        return new MessageResult(true, "删除成功");
    }


    /**
     * 获取分页对象
     * @param current
     * @param pageSize
     */
    private IPage<Project> getIPage(int current, int pageSize) {
        IPage<Project> ipage = new Page<>();
        ipage.setCurrent(current);
        ipage.setSize(pageSize);
        return ipage;
    }




    /**
     * 获取一个项目的所有commit操作
     */
    class  ProjectInfo implements Runnable{

        int pid = 0;
        public ProjectInfo(int pid){
            this.pid = pid;
        }
        @Override
        public void run() {
            List<Collaborators> collaborators = collaboratorsService.getByPid(pid);
            for (Collaborators collaborator :collaborators){
                if (StringUtils.hasLength(collaborator.getName()) && StringUtils.hasLength(collaborator.getUrl())) {
                    collaboratorsService.updateUrlCommitAndCache(collaborator);
                }
            }
        }
    }
}


