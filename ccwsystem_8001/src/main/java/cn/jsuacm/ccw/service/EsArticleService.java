package cn.jsuacm.ccw.service;

import cn.jsuacm.ccw.pojo.Article;
import cn.jsuacm.ccw.pojo.enity.ArticleEsEmpty;
import cn.jsuacm.ccw.pojo.enity.PageResult;

import java.util.List;

/**
 * @ClassName EsArticleService
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/25 14:18
 */
public interface EsArticleService {
    /**
     * 根据关键字搜索文章
     * @param keyWord 关键字
     * @param current 当前页面
     * @param pageSize 页面的大小
     * @return
     */
    public PageResult<ArticleEsEmpty> findArticle(String keyWord, int current, int pageSize);


    /**
     * 通过用户名称的关键字搜索文章
     * @param keyWord 关键字
     * @param current 当前页面
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<ArticleEsEmpty> findByUsername(String keyWord, int current, int pageSize);


    /**
     * 批量保存文章
     * @param articles 文章列表
     * @return 返回是否保存成功
     */
    public boolean saves(List<Article> articles);


    /**
     * 保存一个文章信息到es
     * @param aricle 文章信息对象
     * @return 返回是否保存成功
     */
    public boolean save(Article aricle);


    /**
     * 通过id删除一个es文档
     * @param id  文章的id ， 同时也是es的标识id
     * @return
     */
    public boolean deleteById(int id);




}
