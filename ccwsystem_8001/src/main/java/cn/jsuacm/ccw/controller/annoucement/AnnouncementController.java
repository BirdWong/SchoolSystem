package cn.jsuacm.ccw.controller.annoucement;

import cn.jsuacm.ccw.pojo.announcement.Announcement;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.announcement.AnnouncementService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName AnnouncementController
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/23 22:31
 */
@RestController
@RequestMapping("announcement")
@Api(value = "公告文章操作")
public class AnnouncementController {


    @Autowired
    private AnnouncementService announcementService;


    /**
     * 添加一篇新的公告
     * @param map
     * @param req
     * @return
     */
    @PostMapping("admin/add")
    @ApiOperation(value = "添加一篇新的公告, 插入正确返回id", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "title标题不为空长度长度不大于50字", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "htmlContent", value = "html文章内容不为空", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "markdown文本不为空", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "cid", value = "cid必须存在,公告的任何操作只能操作自己权限能够操作的分类， （自己的权限>分类权限）", required = true, dataType = "int", paramType = "query")
    })
    public MessageResult add(@RequestBody Map<String, Object> map, HttpServletRequest req){
        try {
            int uid = Integer.valueOf(String.valueOf(map.get("uid")));
            if (CheckUserUtil.isUser(req, uid)){
                    Announcement announcement = new Announcement();
                    Object title = map.get("title");
                    Object htmlContent = map.get("htmlContent");
                    Object content = map.get("content");
                    Object cid = map.get("cid");
                    announcement.setTitle(String.valueOf(title));
                    announcement.setHtmlContent(String.valueOf(htmlContent));
                    announcement.setContent(String.valueOf(content));
                    announcement.setCid(Integer.valueOf(String.valueOf(cid)));
                    return announcementService.add(uid, announcement);
            }else {
                return new MessageResult(false, "非本人操作");
            }
        }catch (Exception e){
            return new MessageResult(false, "请正确填写数据");
        }
    }


    /**
     * 更新公告内容
     * @param map
     * @param req
     * @return
     */
    @PostMapping(value = "admin/update")
    @ApiOperation(value = "更新公告信息", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "公告id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "title标题不为空长度长度不大于50字", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "htmlContent", value = "html文章内容不为空", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "markdown文本不为空", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "cid", value = "cid必须存在,公告的任何操作只能操作自己权限能够操作的分类， （自己的权限>分类权限）", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "createTime", value = "创建时间", required = true, dataType = "date", paramType = "query")
    })
    public MessageResult update(@RequestBody Map<String, Object> map, HttpServletRequest req){
        try {
            int uid = Integer.valueOf(String.valueOf(map.get("uid")));
            if (CheckUserUtil.isUser(req, uid)){
                Announcement announcement = new Announcement();
                Object id = map.get("id");
                Object title = map.get("title");
                Object htmlContent = map.get("htmlContent");
                Object content = map.get("content");
                Object cid = map.get("cid");
                Object createTime = map.get("createTime");
                announcement.setId(Integer.valueOf(String.valueOf(id)));
                announcement.setTitle(String.valueOf(title));
                announcement.setHtmlContent(String.valueOf(htmlContent));
                announcement.setContent(String.valueOf(content));
                announcement.setCid(Integer.valueOf(String.valueOf(cid)));
                announcement.setCreateTime(new Date(String.valueOf(createTime)));
                return announcementService.update(uid, announcement);
            }else {
                return new MessageResult(false, "非本人操作");
            }
        }catch (Exception e){
            return new MessageResult(false, "请正确填写数据");
        }
    }


    /**
     * 删除一篇公告
     * @param uid 用户的id
     * @param id 公告id
     * @param req
     * @return
     */
    @GetMapping("admin/delete/{uid}/{id}")
    @ApiOperation(value = "删除一篇公告", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "文章id", required = true, dataType = "int", paramType = "path")
    })
    public MessageResult delete(@PathVariable(value = "uid") int uid,@PathVariable(value = "id") int id, HttpServletRequest req){
        if (CheckUserUtil.isUser(req, uid)){
            return announcementService.delete(uid, id);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


    /**
     * 通过一个公告id获取这个公告，
     * @param uid 用户id， 如果没有登录填充0
     * @param id 公告的id
     * @return
     */
    @GetMapping("getById/{uid}/{id}")
    @ApiOperation(value = "通过公告的id获取一篇公告, 如果获取不到或者不够权限问题则返回空", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id， 如果没有登录则填充0", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "文章id", required = true, dataType = "int", paramType = "path")
    })
    public Announcement getForId(@PathVariable(value = "uid") int uid, @PathVariable(value = "id") int id, HttpServletRequest req){
        if (uid == 0 || CheckUserUtil.isUser(req, uid)) {
            return announcementService.getForId(uid, id);
        }else {
            return null;
        }
    }


    /**
     * 分页获取某个分类的公告， 因为涉及两个id参数担心get方式url写不了这么长，改为put, 如果无法查看或者查询不到内容，会返回一个total为0， context内容长度为0的分页对象
     * @param req
     * @param uid 用户id， 没有则填充0
     * @param cid 分类id
     * @param row 当前页
     * @param pageSize 页面大小
     * @return
     */
    @PutMapping("getPages")
    @ApiOperation(value = "分页获取某个分类的公告", notes = "分页获取某个分类的公告， 因为涉及两个id参数担心get方式url写不了这么长，改为put, 如果无法查看或者查询不到内容，会返回一个total为0， context内容长度为0的分页对象", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id, 如果用户没有登录填充0", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "cid", value = "分类的id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "row", value = "页面数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataType = "int", paramType = "query")
    })
    public PageResult<Announcement> getPages(HttpServletRequest req, @RequestParam(value = "uid") int uid,@RequestParam(value = "cid") int cid,@RequestParam(value = "row") long row,@RequestParam(value = "pageSize") long pageSize){
        if (uid == 0 || CheckUserUtil.isUser(req, uid)){
            return announcementService.getPages(uid, cid, row, pageSize);
        }else {
            return new PageResult<Announcement>(0L,0L,0L,new ArrayList<Announcement>());
        }
    }

}
