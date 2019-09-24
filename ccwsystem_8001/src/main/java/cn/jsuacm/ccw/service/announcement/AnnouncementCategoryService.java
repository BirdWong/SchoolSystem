package cn.jsuacm.ccw.service.announcement;

import cn.jsuacm.ccw.pojo.announcement.AnnouncementCategory;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @ClassName AnnouncementCategoryService
 * @Description 公告分类
 * @Author h4795
 * @Date 2019/09/21 22:15
 */
public interface AnnouncementCategoryService extends IService<AnnouncementCategory>{


    /**
     * 添加公告的分类，分类中确认这个用户的权限高于或者等于分类中设置的权限
     * @param uid 用户的id
     * @param announcementCategory 分类
     * @return
     */
    public MessageResult add(int uid, AnnouncementCategory announcementCategory);

    /**
     * 通过一个id获取一个分类的所有信息
     * @param id 分类的id
     * @return
     */
    public AnnouncementCategory getForId(int id);


    /**
     * 修改分类的信息
     * @param announcementCategory 分类的信息
     * @param uid 用户的id
     * @return
     */
    public MessageResult update(int uid,AnnouncementCategory announcementCategory);


    /**
     * 通过分类的id，删除一个分类
     * @param uid 用户的id
     * @param id 分类的id
     * @return
     */
    public MessageResult delete(int uid, int id);


    /**
     * 获得ALL权限的公告分类
     * @return
     */
    public List<AnnouncementCategory> getOfAll();


    /**
     * 获得ING权限能够看到的公告分类
     * @param uid  用户的id
     * @return
     */
    public List<AnnouncementCategory> getOfIng(int uid);


    /**
     * 获得ADMIN权限能够看到的公告分类
     * @param uid  用户的id
     * @return
     */
    public List<AnnouncementCategory> getOfAdmin(int uid);


    /**
     * 获得Administrator权限能够看到的公告分类
     * @param uid  用户的id
     * @return
     */
    public List<AnnouncementCategory> getOfAdministrator(int uid);



    /**
     * 比较用户权限信息
     * @param uid 用户的id
     * @param role 查看的权限大小
     * @return
     */
    public boolean compareToRole(int uid, AnnouncementCategory.Role role);
}
