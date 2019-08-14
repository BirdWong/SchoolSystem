package cn.jsuacm.ccw.controller.blog;

import cn.jsuacm.ccw.pojo.blog.ArticleCollection;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.blog.ArticleCollectionService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName ArticleCollectionController
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 14:44
 */
@RestController
@RequestMapping(value = "collection")
@Api(value = "用户的收藏操作")
public class ArticleCollectionController {


    @Autowired
    private ArticleCollectionService articleCollectionService;


    /**
     * 添加一个用户收藏信息
     * @param req
     * @param articleCollection
     * @return
     */
    @PostMapping("add")
    @ApiOperation(value = "添加一个收藏信息", notes = "添加一个收藏信息， aid是文章信息id， 不是文章id, 会进行二次token验证， 请确保是本人操作", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章信息id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "query")
    })
    public MessageResult addCollection(HttpServletRequest req, @RequestBody ArticleCollection articleCollection){
        if (CheckUserUtil.isUser(req, articleCollection.getUid())){
            return articleCollectionService.addCollection(articleCollection);
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }


    /**
     * 通过一个id删除收藏信息， 必须本人操作
     * @param req
     * @param uid 用户的id
     * @param id 收藏的id
     * @return
     */
    @GetMapping(value = "deleteById/{uid}/{id}")
    @ApiOperation(value = "通过收藏id删除一条记录", notes = "通过文章id删除一条记录， 请确保这个记录时这个人的, 会进行二次验证", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "id", required = true, value = "收藏记录的id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteById(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "id")int id){
        if (CheckUserUtil.isUser(req, uid)){
            return articleCollectionService.deleteById(id, uid);
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }


    /**
     * 通过文章信息id删除收藏记录，
     * @param req
     * @param aid 文章信息id
     * @param uid 用户的id
     * @return
     */
    @GetMapping(value = "deleteByAid/{aid}/{uid}")
    @ApiOperation(value = "通过文章信息的id删除收藏记录", notes = "通过文章信息的id删除收藏记录， 用于删除文章的时候使用", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章信息id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteByAid(HttpServletRequest req, @PathVariable(value = "aid") int aid, @PathVariable("uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleCollectionService.deleteByAid(aid, uid);
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }


    /**
     * 管理员通过用户的id删除用户的信息
     * @param uid
     * @return
     */
    @GetMapping("admin/deleteByUid/{uid}")
    @ApiOperation(value = "通过用户的id删除所有的关注信息", notes = "管理员用于删除用户时使用", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteByUid(@PathVariable(value = "uid")int uid){
        return articleCollectionService.deleteByUid(uid);
    }


    /**
     * 通过用户的id获取这个用户的收藏信息表
     * @param req
     * @param uid
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping("getByUid/{uid}/{current}/{pageSize}")
    @ApiOperation(value = "获取一个用户的收藏信息列表", notes = "获取一个用户的收藏信息列表， 会进行token二次验证", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "current", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的数量", dataType = "int", paramType = "path")
    })
    public PageResult<ArticleCollection> getByUid(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "current") int current, @PathVariable(value = "pageSize")int pageSize ){
        if (CheckUserUtil.isUser(req, uid)){
            return articleCollectionService.getByUid(uid, current, pageSize);
        }else {
            return new PageResult<>();
        }
    }




    /**
     * 通过文章信息的id获取文章被收藏的信息
     * @param req
     * @param uid
     * @param aid
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping("getByUid/{uid}/{aid}/{current}/{pageSize}")
    @ApiOperation(value = "获取一篇文章被收藏列表", notes = "获取一篇文章被收藏列表， 会进行token二次验证", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "current", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的数量", dataType = "int", paramType = "path")
    })
    public PageResult<ArticleCollection> getByAid(HttpServletRequest req, @PathVariable(value = "aid") int aid,@PathVariable(value = "uid") int uid, @PathVariable(value = "current") int current, @PathVariable(value = "pageSize")int pageSize ){
        if (CheckUserUtil.isUser(req, uid)){
            return articleCollectionService.getByAid(aid, uid, current, pageSize);
        }else {
            return new PageResult<>();
        }
    }





}
