package cn.jsuacm.ccw.service.projects;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.projects.Commit;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @ClassName CommitService
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 10:55
 */
public interface CommitService extends IService<Commit>{


    /**
     * 从github上获取一个项目链接和用户的所有commit
     * @param url
     * @param username
     * @return
     */
    public List<Commit> getByGitHub(String url, String username);


    /**
     * 插入commit数据
     * @param commits
     * @return
     */
    public void insertCommit(Set<Commit> commits);


    /**
     * 通过一条开发成员记录删除这个成员在这个项目中的所有提交记录
     * @param id
     * @return
     */
    public MessageResult deleteByCid(int id);


    /**
     * 通过一个项目成员id查找
     * @param cid
     * @return
     */
    public List<Commit> findByCid(int cid);
}
