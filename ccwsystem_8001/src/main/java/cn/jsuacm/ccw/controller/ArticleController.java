package cn.jsuacm.ccw.controller;

import cn.jsuacm.ccw.pojo.Article;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.ArticleService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    public MessageResult addUserArticle(HttpServletRequest req, @RequestBody Map<String, Object> map){

        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            Article article = new Article();
            article.setViews(0);
            article.setUid(uid);
            article.setContext(String.valueOf(map.get("context")));
            article.setHtmlContext(String.valueOf(map.get("httpContext")));
            article.setKind(Integer.valueOf(String.valueOf(map.get("kind"))));
            article.setStatus(Integer.valueOf(String.valueOf("status")));
            article.setTitle(String.valueOf("title"));
            MessageResult messageResult = articleService.addUserArticle(uid, article);
            return messageResult;
        }else {
            return new MessageResult(false, "没有权限");
        }
    }


    /**
     * 用户获取内容编辑修改自己的文章， 必须强校验是否就是用户自己,如果该用户没有这篇文章， 或者没有对应的权限将会返回空
     * @param req
     * @param map 包含用户的id和文章的id
     * @return
     */
    @GetMapping(value = "getUpdateArticle")
    public Article getUpdateArticle(HttpServletRequest req, @RequestBody Map<String, Object> map){

        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            int aid = Integer.valueOf(String.valueOf(map.get("aid")));
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
    public MessageResult updateUserArticle(HttpServletRequest req, @RequestBody Map<String, Object> map){
        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            Article article = new Article();
            article.setUid(uid);
            article.setContext(String.valueOf(map.get("context")));
            article.setHtmlContext(String.valueOf(map.get("httpContext")));
            article.setKind(Integer.valueOf(String.valueOf(map.get("kind"))));
            article.setStatus(Integer.valueOf(String.valueOf("status")));
            article.setTitle(String.valueOf("title"));
            MessageResult messageResult = articleService.updateUserArticle(uid, article);
            return messageResult;
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
     * @param map
     * @return
     */
    public MessageResult deleteArticle(HttpServletRequest req, @RequestBody Map<String, Object> map){
        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            int aid = Integer.valueOf(String.valueOf(map.get("aid")));
            MessageResult messageResult = articleService.deleteArticle(uid, aid);
            return messageResult;
        }else {
            return new MessageResult(false, "没有操作权限");
        }
    }


    /**
     * 获取用户的所有文章阅读数量
     * @param uid 用户的id
     * @return
     */
    @GetMapping(value = "views/{uid}")
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
    public PageResult<Article> getUserArticleList(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "row") int row, @PathVariable(value = "pageSize") int pageSize){
        if(CheckUserUtil.isUser(req,uid)){
            PageResult<Article> pageResult = articleService.getUserArticleList(uid, row, pageSize);
            return pageResult;
        }else {
            return null;
        }
    }


    /**
     * 返回这个用户的文章归档， 自动忽略用户的非公共文章和非用户文章
     * @param uid 用户的id
     * @return
     */
    @GetMapping(value = "getUserArchive/{uid}")
    public TreeMap<String, LinkedList<Article>> getUserArchive(@PathVariable(value = "uid") int uid){
        TreeMap<String, LinkedList<Article>> userArchive = articleService.getUserArchive(uid);
        return userArchive;
    }


    /**
     * 获取这个用户最近的文章，自动过滤非用户公开和非用户类型文章
     * @param uid 用户的id
     * @param size 获取的数量
     * @return
     */
    @GetMapping(value = "getNewArticle/{uid}/{size}")
    public List<Article> getNewArticle(@PathVariable(value = "uid") int uid, @PathVariable(value = "size") int size){

        List<Article> newArticle = articleService.getNewArticle(uid, size);
        return newArticle;
    }











}
