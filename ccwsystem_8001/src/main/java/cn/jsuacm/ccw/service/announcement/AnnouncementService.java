package cn.jsuacm.ccw.service.announcement;

import cn.jsuacm.ccw.pojo.announcement.Announcement;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @ClassName AnnouncementService
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/23 20:58
 */
public interface AnnouncementService extends IService<Announcement>{


    /**
     * 添加一篇公告
     * @param uid 用户的id
     * @param announcement 公告内容
     * @return
     */
    public MessageResult add(int uid, Announcement announcement);


    /**
     * 通过公告id删除公告
     * @param id 公告id
     * @return
     */
    public MessageResult delete(int uid, int id);


    /**
     * 更新公告
     * @param uid 用户的id
     * @param announcement 公告内容
     * @return
     */
    public MessageResult update(int uid, Announcement announcement);


    /**
     * 通过id获取文章
     * @param uid 用户的id
     * @param id 文章的id
     * @return
     */
    public Announcement getForId(int uid, int id);


    /**
     * 分页获取公告列表
     * @param uid 用户id 如果没登录则发送0
     * @param cid 分类id
     * @param row 页面数
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<Announcement> getPages(int uid, int cid, long row, long pageSize);


    /**
     * 确认是否有属于某个cid的文章
     * @param cid 分类的id
     * @return
     */
    public boolean hasOfCid(int cid);
}
