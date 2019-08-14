package cn.jsuacm.ccw.service.projects.impl;

import cn.jsuacm.ccw.mapper.projects.CollaboratorsMapper;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.projects.Collaborators;
import cn.jsuacm.ccw.pojo.projects.Commit;
import cn.jsuacm.ccw.service.projects.CollaboratorsService;
import cn.jsuacm.ccw.service.projects.CommitService;
import cn.jsuacm.ccw.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName CollaboratorsServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 21:56
 */
@Service
public class CollaboratorsServiceImpl extends ServiceImpl<CollaboratorsMapper, Collaborators> implements CollaboratorsService {



    @Autowired
    private CollaboratorsMapper collaboratorsMapper;

    @Autowired
    private CommitService commitService;



    @Autowired
    private RedisUtil redisUtil;

    /**
     * 为一个项目添加成员
     *
     * @param collaborators
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult addCollaborators(Set<Collaborators> collaborators) {
        int pid = 0;

        for (Collaborators collaborator : collaborators) {
            if (pid == 0) {
                pid = collaborator.getPid();
            } else if (pid != collaborator.getPid()) {
                throw new RuntimeException("不是同一个项目");
            }
            boolean save = save(collaborator);
            if (!save){
                throw new RuntimeException("保存失败， 请检查是否同一个项目uid重复");
            }
            redisUtil.hset(pid+"", collaborator.getUid() + "", new HashSet<>());
        }

        return new MessageResult(true, "保存成功");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult deleteById(int id) {
        Collaborators c = getById(id);
        commitService.deleteByCid(id);
        boolean remove = removeById(id);
        if (remove) {
            Map<Object, Object> map = redisUtil.hmget(c.getPid() + "");
            if (map != null) {
                redisUtil.hdel(c.getPid()+"", c.getUid()+"");
            }
        }
        return new MessageResult(true, "删除成功");
    }

    /**
     * 查看一个项目的人员列表
     *
     * @param pid 项目id
     * @return
     */
    @Override
    public List<Collaborators> getByPid(int pid) {
        QueryWrapper<Collaborators> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", pid);
        List<Collaborators> collaborators = collaboratorsMapper.selectList(queryWrapper);
        return collaborators;
    }

    /**
     * 查看一个人的项目列表
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public List<Collaborators> getByUid(int uid) {
        QueryWrapper<Collaborators> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        List<Collaborators> collaborators = collaboratorsMapper.selectList(queryWrapper);
        return collaborators;
    }

    /**
     * 更新用户的项目地址和用户名称
     *
     * @param url  地址
     * @param name 名称
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResult updateUrl(int id, int uid,String url, String name) {
        Collaborators collaborators = getById(id);
        if (collaborators == null || collaborators.getUid() != uid){
            return new MessageResult(false, "非本人操作");
        }
        UpdateWrapper<Collaborators> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("url", url).set("name", name);
        boolean update = update(updateWrapper);
        if (update){
            collaborators.setName(name);
            collaborators.setUrl(url);
            updateUrlCommitAndCache(collaborators);
            return new MessageResult(update, "修改成功");
        }else {
            return new MessageResult(update, "修改失败");
        }

    }


    /**
     * 通过项目id， 项目链接， 项目的名称修改项目提交信息以及更新redis 缓存
     * @param collaborators
     */
    @Override
    public void updateUrlCommitAndCache(Collaborators collaborators) {
        Map<Object, Object> map = redisUtil.hmget(collaborators.getPid() + "");
        if (map == null){
            map = new HashMap<>();
        }
        Object hget = redisUtil.hget(collaborators.getPid() + "", collaborators.getUid() + "");
        if (hget == null){
            hget = new HashSet<String>();
        }
        Set<String> set = (Set<String>) hget;
        try {
            List<Commit> commits = commitService.getByGitHub(collaborators.getUrl(), collaborators.getName());
            for (Commit commit : commits){
                if (!set.contains(commit.getSha())){
                    set.add(commit.getSha());
                    try {
                        commitService.save(commit);
                    }catch (Exception e){
                        continue;
                    }
                }else {
                    break;
                }
            }

            redisUtil.hset(collaborators.getPid()+ "", collaborators.getUid()+"", set);
        }catch (Exception e){
            throw new RuntimeException("保存commit出现错误");
        }
    }

    /**
     * 通过用户的id从所有项目中删除这个用户
     *
     * @param uid
     * @return
     */
    @Override
    public MessageResult deleteByUid(int uid) {

        QueryWrapper<Collaborators> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        List<Collaborators> collaborators = collaboratorsMapper.selectList(queryWrapper);
        for (Collaborators collaborator : collaborators){
            deleteById(collaborator.getId());
        }
        return new MessageResult(true, "删除成功");
    }


}
