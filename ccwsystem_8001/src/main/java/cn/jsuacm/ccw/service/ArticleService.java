package cn.jsuacm.ccw.service;

import cn.jsuacm.ccw.pojo.Article;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * @ClassName ArticleService
 * @Description 文章服务层接口
 * @Author h4795
 * @Date 2019/06/18 22:22
 */
public interface ArticleService extends IService<Article>{

    /**
     * 添加用户的文章，kind类型必须为0
     * @param article 文章实体类
     * @param uid 用户的id
     * @return
     */
    public MessageResult addUserArticle(int uid, Article article);


    /**
     * 获取一篇文章的所有内容用于修改
     * @param uid 用户id
     * @param aid 文章id
     * @return
     */
    public Article getUpdateArticle(int uid, int aid);


    /**
     * 更新文章
     * @param uid 用户的id
     * @param article 更新的文章
     * @return
     */
    public MessageResult updateUserArticle(int uid, Article article);


    /**
     * 修改文章的状态
     * @param uid 用户的id
     * @param aid 文章的id
     * @param status 文章的状态
     * @return
     */
    public MessageResult changeStatus(int uid, int aid, int status);


    /**
     * 彻底删除一篇文章
     * @param uid 用户id
     * @param aid 文章id
     * @return
     */
    public MessageResult deleteArticle(int uid, int aid);


    /**
     * 查看这个用户一共有多少浏览量
     * @param uid
     * @return
     */
    public MessageResult getViews(int uid);


    /**
     * 查看一篇文章，只允许查看用户的文章和发布的文章
     * @param aid 文章id
     * @return
     */
    public Article getUserArticle(int aid);


    /**
     * 获取一个用户的文章的列表
     * @param uid
     * @return
     */
    public PageResult<Article> getUserArticleList(int uid, int row, int pageSize);


    /**
     * 获取用户发表的公开，用户个人的文章归档
     * @param uid
     * @return
     */
    public TreeMap<String, LinkedList<Article>> getUserArchive(int uid);


    /**
     * 获取用户最近发表的文章, 过滤非公开和非用户类型文章
     * @param uid 用户的id
     * @param size 需要的文章的数量
     * @return
     */
    public List<Article> getNewArticle(int uid, int size);
}
