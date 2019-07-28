package cn.jsuacm.ccw.controller;

import cn.jsuacm.ccw.pojo.Article;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.ArticleService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @ClassName ArticleController
 * @Description 文章控制层
 * @Author h4795
 * @Date 2019/06/18 22:21
 */
@RestController
@RequestMapping(value = "article")
@Api(value = "article", description = "文章内容的操作")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    /**
     * 用户添加自己的文章，必须强行校验是否就是用户自己
     * @param req
     * @param map
     * @return
     */
    @PostMapping(value = "addUserArticle")
    @ApiOperation(value = "用户添加一篇博客", notes = "用户添加一篇自己的博客， 只允许是用户属性的文章,会有token二次校验", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "content", required = true, value = "markdowm文本内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "htmlContent", required = true, value = "html文本内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", required = true, value = "文章的状态", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "title", required = true, value = "文章的标题", dataType = "int", paramType = "query")
    })
    public MessageResult addUserArticle(HttpServletRequest req, @RequestBody Map<String, Object> map){

        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            Article article = new Article();
            article.setViews(0);
            article.setUid(uid);
            article.setContent(String.valueOf(map.get("content")));
            article.setHtmlContent(String.valueOf(map.get("htmlContent")));
            article.setStatus(Integer.valueOf(String.valueOf("status")));
            article.setTitle(String.valueOf("title"));
            MessageResult messageResult = articleService.addUserArticle(uid, article);
            return messageResult;
        }else {
            return new MessageResult(false, "没有权限");
        }
    }


    /**
     * 用户获取内容编辑修改自己的文章，
     * 必须强校验是否就是用户自己,如果该用户没有这篇文章，
     * 或者没有对应的权限将会返回空
     * @param req
     * @param uid 用户的id
     * @param aid 文章的id
     * @return
     */
    @GetMapping(value = "getUpdateArticle/{uid}/{aid}")
    @ApiOperation(value = "获取一篇文章的详细信息用于更改", notes = "用户获取内容编辑修改自己的文章，必须强校验是否就是用户自己,如果该用户没有这篇文章，或者没有对应的权限将会返回空",httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "path")
    })
    public Article getUpdateArticle(HttpServletRequest req, @PathVariable(value = "uid")int uid, @PathVariable(value = "aid") int aid){

        if (CheckUserUtil.isUser(req, uid)){
            Article article = articleService.getUpdateArticle(uid, aid);
            return article;
        }else {
            return null;
        }
    }


    /**
     * 用户更新自己的文章，必须强教研是否用户就是自己
     * @param req
     * @param map
     * @return
     */
    @PostMapping(value = "updateUserArticle")
    @ApiOperation(value = "更新一篇用户文章", notes = "用户更新自己的文章，必须强教研是否用户就是自己", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "content", required = true, value = "markdowm文本内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "htmlContent", required = true, value = "html文本内容", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "status", required = true, value = "文章的状态", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "title", required = true, value = "文章的标题", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "kind", required = true, value = "文章的类型", dataType = "int", paramType = "query")
    })
    public MessageResult updateUserArticle(HttpServletRequest req, @RequestBody Map<String, Object> map){
        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            Article article = new Article();
            article.setUid(uid);
            article.setContent(String.valueOf(map.get("content")));
            article.setHtmlContent(String.valueOf(map.get("htmlContent")));
            article.setKind(Integer.valueOf(String.valueOf(map.get("kind"))));
            article.setStatus(Integer.valueOf(String.valueOf("status")));
            article.setTitle(String.valueOf("title"));
            return articleService.updateUserArticle(uid, article);
        }else {
            return new MessageResult(false, "没有操作权限");
        }
    }


    /**
     * 修改文章的状态，必须验证是否是本人的文章
     * @param req
     * @param map
     * @return
     */
    @PostMapping(value = "changeStatus")
    @ApiOperation(value = "修改文章状态", notes = "修改文章的状态，必须验证是否是本人的文章", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "post"),
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "post"),
            @ApiImplicitParam(name = "status", required = true, value = "文章的状态", dataType = "int", paramType = "post")
    })
    public MessageResult changeStatus(HttpServletRequest req, @RequestBody Map<String, Object> map){
        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            int aid = Integer.valueOf(String.valueOf(map.get("aid")));
            int status = Integer.valueOf(String.valueOf("status"));
            MessageResult messageResult = articleService.changeStatus(uid, aid, status);
            return messageResult;
        }else {
            return new MessageResult(false, "没有操作权限");
        }
    }


    /**
     * 删除文章，必须确认是否是本人的文章
     * @param req
     * @param uid 用户的id
     * @param aid 文章的id
     * @return
     */
    @ApiOperation(value = "删除文章", notes = "删除文章，必须确认是否是本人的文章", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "path")
    })
    @GetMapping(value = "delete/{uid}/{aid}")
    public MessageResult deleteArticle(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "aid") int aid){
        if (CheckUserUtil.isUser(req, uid)){
            MessageResult messageResult = articleService.deleteArticle(uid, aid);
            return messageResult;
        }else {
            return new MessageResult(false, "没有操作权限");
        }
    }


    /**
     * 文章阅读数量
     * @param aid 文章的id
     * @return
     */
    @GetMapping(value = "view/{aid}")
    @ApiOperation(value = "查看文章阅读数", notes = "查看一篇文章的阅读数量", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "path")
    })
    public MessageResult getView(@PathVariable(value = "aid") int aid){
        MessageResult view = articleService.getView(aid);
        return view;
    }



    /**
     * 获取用户的所有文章阅读数量
     * @param uid 用户的id
     * @return
     */
    @GetMapping(value = "views/{uid}")
    @ApiOperation(value = "查看用户总阅读数", notes = "查看一个用户的总阅读数量", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult getViews(@PathVariable(value = "uid") int uid){
        MessageResult views = articleService.getViews(uid);
        return views;
    }


    /**
     * 获取一篇用户公开的文章
     * @param aid  文章的id
     * @return
     */
    @GetMapping("userArticle/{aid}")
    @ApiOperation(value = "获取一篇用户公开的文章", notes = "获取一篇用户公开的文章",httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章id", dataType = "int", paramType = "path")
    })
    public Article getUserArticle(@PathVariable(value = "aid") int aid){
        Article userArticle = articleService.getUserArticle(aid);
        return userArticle;
    }


    /**
     * 获取一个用户的所有文章， 必须添加验证是否是这个用户
     * @param req
     * @param uid 用户id
     * @return
     */
    @GetMapping("getUserArticleList/{uid}/{row}/{pageSize}")
    @ApiOperation(value = "分页获取一个用户的所有文章", notes = "获取一个用户的所有文章， 必须添加验证是否是这个用户", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int",paramType = "path")
    })
    public PageResult<Article> getUserArticleList(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "row") int row, @PathVariable(value = "pageSize") int pageSize){
        if(CheckUserUtil.isUser(req,uid)){
            return articleService.getUserArticleList(uid, row, pageSize);
        }else {
            return null;
        }
    }


    /**
     * 管理员获取一个用户的所有文章列表
     * @param uid
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping("admin/getUserArticleList/{uid}/{current}/{pageSize}")
    @ApiOperation(value = "管理员分页获取用户的所有文章列表", notes = "管理员分页获取用户的所有文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int",paramType = "path")
    })
    public PageResult<Article> getUserArticleListByAdmin(@PathVariable(value = "uid") int uid, @PathVariable(value = "current") int current,@PathVariable("pageSize") int pageSize){
        return articleService.getUserArticleList(uid, current, pageSize);
    }

    /**
     * 获取一个用户公开的文章列表
     * @param uid 用户id
     * @param row 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping("getUserPublicArticleList/{uid}/{row}/{pageSize}")
    @ApiOperation(value = "获取一个用户公开的文章列表", notes = "获取一个用户公开的文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int",paramType = "path")
    })
    public PageResult<Article> getUserPublicArticleList(@PathVariable(value = "uid") int uid, @PathVariable(value = "row") int row, @PathVariable(value = "pageSize") int pageSize){
        return articleService.getUserPublicArticleList(uid, row, pageSize);
    }


    /**
     * 获取一个用户私有的文章列表
     * @param req 保存token的信息对象
     * @param uid 用户id
     * @param row 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping("getUserPrivateArticleList/{uid}/{row}/{pageSize}")
    @ApiOperation(value = "用户获取自己私有的文章列表", notes = "用户获取自己私有的文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int",paramType = "path")
    })
    public PageResult<Article> getUserPrivateArticleList(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "row") int row, @PathVariable(value = "pageSize") int pageSize){
        if(CheckUserUtil.isUser(req,uid)){
            return articleService.getUserPrivateArticleList(uid, row, pageSize);
        }else {
            return null;
        }
    }


    /**
     * 管理员获取一个用户的私有文章列表
     * @param uid 用户id
     * @param row 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping("admin/getUserPrivateArticleList/{uid}")
    @ApiOperation(value = "管理员获取一个用户私有的文章列表", notes = "管理员获取一个用户私有的文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int",paramType = "path")
    })
    public PageResult<Article> adminGetUserPrivateArticleList(@PathVariable(value = "uid")int uid,@PathVariable(value = "row") int row, @PathVariable(value = "pageSize") int pageSize){
        return articleService.getUserPrivateArticleList(uid, row, pageSize);
    }

    /**
     * 获取一个用户草稿箱的文章列表
     * @param req 保存token的信息对象
     * @param uid 用户id
     * @param row 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping("getUserDraftArticleList/{uid}/{row}/{pageSize}")
    @ApiOperation(value = "获取一个用户草稿箱的文章列表", notes = "获取一个用户草稿箱的文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int",paramType = "path")
    })
    public PageResult<Article> getUserDraftArticleList(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "row") int row, @PathVariable(value = "pageSize") int pageSize){
        if(CheckUserUtil.isUser(req,uid)){
            return articleService.getUserDraftArticleList(uid, row, pageSize);
        }else {
            return null;
        }
    }



    /**
     * 管理员获取一个用户的草稿文章列表
     * @param uid 用户id
     * @param row 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping("admin/getUserDraftArticleList/{uid}")
    @ApiOperation(value = "管理员获取一个用户草稿的文章列表", notes = "管理员获取一个用户草稿的文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int",paramType = "path")
    })
    public PageResult<Article> adminGetUserDraftArticleList(@PathVariable(value = "uid")int uid,@PathVariable(value = "row") int row, @PathVariable(value = "pageSize") int pageSize){
        return articleService.getUserDraftArticleList(uid, row, pageSize);
    }


    /**
     * 返回这个用户的文章归档， 自动忽略用户的非公共文章和非用户文章
     * @param uid 用户的id
     * @return
     */
    @GetMapping(value = "getUserPublicArchive/{uid}")
    @ApiOperation(value = "获取一个用户公开文章类型的时间归档", notes = "获取一个用户公开类型文章的时间归档，按照月份为单位归档", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public TreeMap<String, LinkedList<Article>> getUserArchive(@PathVariable(value = "uid") int uid){
        return articleService.getUserPublicArchive(uid);
    }


    /**
     * 返回这个用户的私密文章归档， 自动忽略非用户文章
     * @param uid 用户的id
     * @return
     */
    @GetMapping(value = "getUserPrivateArchive/{uid}")
    @ApiOperation(value = "返回这个用户的私密文章归档， 自动忽略非用户文章", notes = "返回这个用户的私密文章归档， 自动忽略非用户文章，按照月份为单位归档", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public TreeMap<String, LinkedList<Article>> getUserPrivateArchive(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)) {
            return articleService.getUserPrivateArchive(uid);
        }else {
            return null;
        }
    }


    /**
     * 返回这个用户的草稿文章归档， 自动忽略非用户文章
     * @param uid 用户的id
     * @return
     */
    @GetMapping(value = "getUserDraftArchive/{uid}")
    @ApiOperation(value = "返回这个用户的草稿文章归档， 自动忽略非用户文章", notes = "返回这个用户的草稿文章归档， 自动忽略非用户文章，按照月份为单位归档", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public TreeMap<String, LinkedList<Article>> getUserDraftArchive(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)) {
            return articleService.getUserDraftArchive(uid);
        }else {
            return null;
        }

    }




    /**
     * 获取这个用户最近的文章，自动过滤非用户公开和非用户类型文章
     * @param uid 用户的id
     * @param size 获取的数量
     * @return
     */
    @GetMapping(value = "getNewArticle/{uid}/{size}")
    @ApiOperation(value = "获取这个用户最近的文章", notes = "自动过滤非用户公开和非用户类型文章", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid" , required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "size", required = true, value = "需要获取的数量", dataType = "int", paramType = "path")
    })
    public List<Article> getNewArticle(@PathVariable(value = "uid") int uid, @PathVariable(value = "size") int size){

        return articleService.getNewArticle(uid, size);
    }


    /**
     * 分页获取最热的文章内容
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping(value = "getHots/{current}/{pageSize}")
    @ApiOperation(value = "分页获取最热的文章", notes = "分页获取最热的文章内容", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int", paramType = "path")
    })
    public PageResult<Article> getHotArticles(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize") int pageSize){
        return articleService.getHotsArticles(current, pageSize);
    }



}
