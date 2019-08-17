package cn.jsuacm.ccw.service.projects;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.pojo.projects.Project;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @ClassName ProjectService
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 19:55
 */
public interface ProjectService extends IService<Project>{

    /**
     * 分页获取所有项目信息
     * @param status 项目的状态
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<Project> getPages(int status, int current, int pageSize);


    /**
     * 添加一个项目
     * @param project
     * @return
     */
    public MessageResult addProject(Project project);


    /**
     * 修改一个项目的信息
     * @param project
     * @return
     */
    public MessageResult updateProject(Project project);


    /**
     * 修改项目状态
     * @param id 项目id
     * @return
     */
    public MessageResult updateStatus(int id);


    /**
     * 通过项目id删除项目
     * @param id
     * @return
     */
    public MessageResult deleteById(int id);



}
