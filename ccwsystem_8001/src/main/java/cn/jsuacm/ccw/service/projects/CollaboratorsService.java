package cn.jsuacm.ccw.service.projects;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.projects.Collaborators;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @ClassName CollaboratorsService
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 21:55
 */
public interface CollaboratorsService extends IService<Collaborators>{


    /**
     * 为一个项目添加成员
     * @param collaborators
     * @return
     */
    public MessageResult addCollaborators(Set<Collaborators> collaborators);


    /**
     * 通过id删除
     * @param id
     * @return
     */
    public MessageResult deleteById(int id);


    /**
     * 查看一个项目的人员列表
     * @param pid 项目id
     * @return
     */
    public List<Collaborators> getByPid(int pid);


    /**
     * 查看一个人的项目列表
     * @param uid 用户id
     * @return
     */
    public List<Collaborators> getByUid(int uid);


    /**
     * 更新用户的项目地址和用户名称
     * @param url  地址
     * @param name 名称
     * @return
     */
    public MessageResult updateUrl(int id, int uid, String url, String name);


    /**
     * 通过项目id， 项目链接， 项目的名称修改项目提交信息以及更新redis 缓存
     * @param collaborators
     */
    public void updateUrlCommitAndCache(Collaborators collaborators);


    /**
     * 通过用户的id从所有项目中删除这个用户
     * @param uid
     * @return
     */
    public MessageResult deleteByUid(int uid);
}
