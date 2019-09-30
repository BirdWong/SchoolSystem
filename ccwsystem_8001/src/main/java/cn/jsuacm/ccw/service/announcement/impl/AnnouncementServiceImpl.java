package cn.jsuacm.ccw.service.announcement.impl;

import cn.jsuacm.ccw.mapper.announcement.AnnouncementMapper;
import cn.jsuacm.ccw.pojo.announcement.Announcement;
import cn.jsuacm.ccw.pojo.announcement.AnnouncementCategory;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.announcement.AnnouncementCategoryService;
import cn.jsuacm.ccw.service.announcement.AnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName AnnouncementServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/23 20:59
 */
@Service

public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Autowired
    private AnnouncementMapper mapper;


    @Autowired
    private AnnouncementCategoryService service;

    /**
     * 添加一篇公告
     *
     * @param uid          用户的id
     * @param announcement 公告内容
     * @return
     */
    @Override
    public MessageResult add(int uid, Announcement announcement) {
        String msg = checkInfo(uid, announcement, true);
        if (msg == null){
            announcement.setCreateTime(new Date());
            announcement.setModifyTime(new Date());
            announcement.setViews(0);
            save(announcement);
            return new MessageResult(true, String.valueOf(announcement.getId()));
        }else {
            return new MessageResult(false, msg);
        }
    }

    /**
     * 通过公告id删除公告
     *
     * @param id 公告id
     * @return
     */
    @Override

    public MessageResult delete(int uid, int id) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("cid");
        queryWrapper.eq("id",id);
        List<Announcement> announcements = mapper.selectList(queryWrapper);
        if (announcements == null || announcements.size() == 0){
            return new MessageResult(false, "没有这篇文章");
        }
        String s = checkCategory(uid, announcements.get(0));
        if (s != null){
            return new MessageResult(false, s);
        }
        boolean isOk = removeById(id);
        if (isOk){
            return new MessageResult(true, "删除成功");
        }else {
            return new MessageResult(false, "删除失败，请稍后再试");
        }
    }

    /**
     * 更新公告
     *
     * @param uid          用户的id
     * @param announcement 公告内容
     * @return
     */
    @Override

    public MessageResult update(int uid, Announcement announcement) {
        String msg = checkInfo(uid, announcement, false);
        if (msg == null){
            announcement.setModifyTime(new Date());
            boolean isOk = updateById(announcement);
            if (isOk){
                return new MessageResult(true, "更新成功");
            }else {
                return new MessageResult(false, "更新失败");
            }
        }
        return new MessageResult(false, msg);
    }

    /**
     * 通过id获取文章
     *
     * @param uid 用户的id
     * @param id  文章的id
     * @return
     */
    @Override

    public Announcement getForId(int uid, int id) {
        Announcement announcement = getById(id);
        if (announcement != null){
            String msg = checkCategory(uid, announcement);
            if (msg == null){
                return announcement;
            }
        }
        return null;
    }

    /**
     * 分页获取公告列表
     *
     * @param uid      用户id 如果没登录则发送0
     * @param cid      分类id
     * @param row      页面数
     * @param pageSize 页面大小
     * @return
     */
    @Override

    public PageResult<Announcement> getPages(int uid, int cid, long row, long pageSize) {

        AnnouncementCategory category = service.getForId(cid);
        // 如果有这个分类
        if (category != null){
            // 如果这个用户有查看的权限
            if (service.compareToRole(uid, category.getRole())){
                // 设置查询条件
                QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("cid", cid);
                // 分页设置
                Page<Announcement> announcementPage = new Page<>();
                announcementPage.setCurrent(row);
                announcementPage.setSize(pageSize);
                // 获取分页结果
                IPage<Announcement> announcementIPage = mapper.selectPage(announcementPage, queryWrapper);
                PageResult<Announcement> announcementPageResult = new PageResult<>();
                // 填充分页
                announcementPageResult.setTatolSize(announcementIPage.getTotal());
                announcementPageResult.setRow(announcementIPage.getCurrent());
                announcementPageResult.setPageSize(announcementIPage.getSize());
                announcementPageResult.setPageContext(announcementIPage.getRecords());
                return announcementPageResult;
            }
        }
        PageResult<Announcement> announcementPageResult = new PageResult<>();
        announcementPageResult.setPageContext(new ArrayList<>());
        announcementPageResult.setRow(row);
        announcementPageResult.setPageSize(pageSize);
        announcementPageResult.setTatolSize(0L);
        return announcementPageResult;
    }

    /**
     * 确认是否有属于某个cid的文章
     *
     * @param cid 分类的id
     * @return
     */
    @Override
    public boolean hasOfCid(int cid) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper();
        queryWrapper.eq("cid", cid);
        Integer count = mapper.selectCount(queryWrapper);

        return count != 0;
    }


    /**
     * 文章要求
     *  1. title标题不为空长度长度不大于50字
     *  2. html文章内容不为空
     *  3. markdown文本不为空
     *  4. cid必须存在
     *  5. 如果是修改更新文章必须有创建时间参数,以及公告id
     *  6. 公告的任何操作只能操作自己权限能够操作的分类， （自己的权限>分类权限）
     *
     * @param announcement 公告信息
     * @param uid 用户id
     * @param isNew 是否是新文章
     * @return
     */
    private String checkInfo(int uid, Announcement announcement, boolean isNew){
        if (checkTitle(announcement)){
            return "标题填写错误：title标题不为空长度长度不大于50字";
        }

        if (announcement.getCid() != 0){
            String x = checkCategory(uid, announcement);
            if (x != null){ return x;}
        }else {
            return "请填写正确分类";
        }

        if (checkContent(announcement.getContent())){
            return "请填写内容";
        }

        if (checkContent(announcement.getHtmlContent())){
            return "请填写内容";
        }

        if (!isNew){
            String x = checkOldInfoOfUpdate(uid, announcement);
            if (x != null){ return x;}

        }


        return null;


    }

    private String checkOldInfoOfUpdate(int uid, Announcement announcement) {
        if (announcement.getId() == 0){
            return "请填写正确文章id";
        }else {
            // 获取旧的文章信息
            Announcement oldAnnouncement = getById(announcement.getId());
            if (oldAnnouncement == null){
                return "没有此公告";
            }
            AnnouncementCategory oldCategory = service.getById(oldAnnouncement.getCid());
            if (oldCategory == null){
                return "服务器内部数据错误，请检查公告id："+announcement.getId()+", 公告id："+announcement.getCid()+"  分类不存在!";
            }else if (!service.compareToRole(uid, oldCategory.getRole())){
                return "没有权限修改此公告";
            }

            if (oldAnnouncement.getCreateTime().compareTo(announcement.getCreateTime()) != 0){
                return "妄图修改创建时间，风险操作";
            }

            // 设置查看量
            announcement.setViews(oldAnnouncement.getViews());
        }
        return null;
    }

    private boolean checkContent(String content) {
        return content == null || content.isEmpty();
    }

    private String checkCategory(int uid, Announcement announcement) {
        AnnouncementCategory category = service.getById(announcement.getCid());
        if (category == null){
            return "没有此分类";
        }
        if (!service.compareToRole(uid, category.getRole())){
            return "没有权限操作这个分类";
        }
        return null;
    }

    private boolean checkTitle(Announcement announcement) {
        return announcement.getTitle() == null || announcement.getTitle().isEmpty() || announcement.getTitle().length() > 50;
    }


}
