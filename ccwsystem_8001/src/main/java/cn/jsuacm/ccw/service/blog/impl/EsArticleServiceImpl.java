package cn.jsuacm.ccw.service.blog.impl;

import cn.jsuacm.ccw.pojo.blog.Article;
import cn.jsuacm.ccw.pojo.blog.ArticleInfomation;
import cn.jsuacm.ccw.pojo.blog.Category;
import cn.jsuacm.ccw.pojo.enity.ArticleEsEmpty;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.EsBasic;
import cn.jsuacm.ccw.service.blog.ArticleInfomationService;
import cn.jsuacm.ccw.service.blog.CategoryService;
import cn.jsuacm.ccw.service.blog.EsArticleRepository;
import cn.jsuacm.ccw.service.blog.EsArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName EsArticleServiceImpl
 * @Description 用于文章相关属性的搜索
 * @Author h4795
 * @Date 2019/07/24 17:13
 */
@Service
@CacheConfig(cacheNames = "es_article")
public class EsArticleServiceImpl extends EsBasic<ArticleEsEmpty> implements EsArticleService{

    @Autowired
    EsArticleRepository repository;

    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate template;

    @Autowired
    private ArticleInfomationService infomationService;

    @Autowired
    private CategoryService categoryService;

    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/getUserById/";

    /**
     * 高亮标签的前缀
     */
    private static final String PRE_TAG = "<em style='color:#dd4b39'>";

    /**
     *  高亮标签的后缀
     */
    private static final String POST_TAG = "</em>";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 根据关键字搜索文章
     * @param keyWord 关键字
     * @param current 当前页面
     * @param pageSize 页面的大小
     * @return
     */
    @Override
    @Cacheable
    public PageResult<ArticleEsEmpty> findArticle(String keyWord, int current, int pageSize){

        // 组合查询，boost即为权重，数值越大，权重越大
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().
                must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("title",keyWord ).boost(3))
                        .should(QueryBuilders.matchQuery("content",keyWord).boost(1))).
                must(QueryBuilders.termQuery("kind",Article.USER_ARTICLE))
                .must(QueryBuilders.termQuery("status",Article.PUBLIC_ARTICLE));

        ArrayList<String> keys = new ArrayList<>();
        keys.add("title");
        keys.add("content");
        return search(queryBuilder, keys, current, pageSize);
    }

    /**
     * 通过用户名称的关键字搜索文章
     *
     * @param keyWord  关键字
     * @param current  当前页面
     * @param pageSize 页面大小
     * @return
     */
    @Override
    @Cacheable
    public PageResult<ArticleEsEmpty> findByUsername(String keyWord, int current, int pageSize) {
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().
                must(QueryBuilders.matchQuery("username", keyWord)).
                must(QueryBuilders.termQuery("status",1)).
                must(QueryBuilders.termQuery("kind", 0));
        ArrayList<String> keys = new ArrayList<>();
        keys.add("username");
        return search(queryBuilder, keys, current, pageSize);
    }

    /**
     * 批量保存文章
     *
     * @param articles 文章列表
     * @return
     */
    @Override
    public boolean saves(List<Article> articles) {
        try {
            for (Article article : articles) {
                repository.save(createArticleEsEmpty(article));
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 保存一个文章信息到es
     *
     * @param article 文章信息对象
     * @return 返回是否保存成功
     */
    @Override
    public boolean save(Article article) {
        try {
            ArticleEsEmpty articleEsEmpty = createArticleEsEmpty(article);
            if (articleEsEmpty == null){
                return false;
            }
            repository.save(articleEsEmpty);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    /**
     * 通过id删除一个es文档
     *
     * @param id 文章的id ， 同时也是es的标识id
     * @return
     */
    @Override
    public boolean deleteById(int id) {
        try {
            repository.deleteById(id);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    /**
     * 如果用户名被修改，修改信息中的用户名
     *
     * @param uid
     * @param username
     * @return
     */
    @Override
    public MessageResult updateUsername(int uid, String username) {
        List<ArticleEsEmpty> esEmpties = repository.findByUid(uid);
        for(ArticleEsEmpty articleEsEmpty : esEmpties){
            articleEsEmpty.setUsername(username);
            repository.save(articleEsEmpty);
        }
        return new MessageResult(true, "修改成功");
    }

    /**
     * 通过一个field名称生成一个对象的set方法名称
     * @param fieldName field名称
     * @return 返回一个set方法名称
     */
    private String parSetName(String fieldName) {
        // 如果是空对象则返回空
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        // 确定开始大写的位置
        int startIndex = 0;
        if (fieldName.charAt(0) == '_') {
            startIndex = 1;
        }
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }




    /**
     * 将搜索的数据转换成实体
     * @param searchHit
     * @return
     */
    @Override
    public ArticleEsEmpty insertInfo(SearchHit searchHit) {
        ArticleEsEmpty data = new ArticleEsEmpty();
        // 公共字段
        data.setId(new Double(searchHit.getId()).intValue());
        data.setContent(String.valueOf(searchHit.getSourceAsMap().get("content")));
        data.setStatus(Integer.valueOf(searchHit.getSourceAsMap().get("status").toString()));
        data.setTitle(String.valueOf(searchHit.getSourceAsMap().get("title")));
        data.setUid(Integer.valueOf(searchHit.getSourceAsMap().get("uid").toString()));
        data.setKind(Integer.valueOf(searchHit.getSourceAsMap().get("kind").toString()));
        data.setCid(Integer.valueOf(searchHit.getSourceAsMap().get("cid").toString()));
        data.setUsername(String.valueOf(searchHit.getSourceAsMap().get("username")));
        data.setCategory(String.valueOf(searchHit.getSourceAsMap().get("AnnouncementCategory")));
        Object createTime = searchHit.getSourceAsMap().get("createtime");

        if (createTime != null) {
            data.setCreatetime(new Date(Long.valueOf(createTime.toString())));
        }
        return data;
    }


    /**
     * 通过文章的信息创建一个文章信息对象
     * @param article
     * @return
     */
    private ArticleEsEmpty createArticleEsEmpty(Article article){
        ArticleEsEmpty articleEsEmpty = new ArticleEsEmpty();
        articleEsEmpty.setId(article.getAid());
        articleEsEmpty.setCreatetime(article.getCreateTime());
        articleEsEmpty.setKind(article.getKind());
        articleEsEmpty.setTitle(article.getTitle());
        articleEsEmpty.setContent(delHTMLTag(article.getHtmlContent()));
        articleEsEmpty.setStatus(article.getStatus());
        articleEsEmpty.setUid(article.getUid());
        try {
            ResponseEntity<Map> entity = template.getForEntity(url + String.valueOf(articleEsEmpty.getUid()), Map.class);
            Map userMap = entity.getBody();
            Object username = userMap.get("username");
            if (username != null) {
                articleEsEmpty.setUsername(String.valueOf(username));
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ArticleInfomation infomation = infomationService.getOne(new QueryWrapper<ArticleInfomation>().eq("aid", article.getAid()));
        articleEsEmpty.setCid(infomation.getCid());
        Category category = categoryService.getById(articleEsEmpty.getCid());
        articleEsEmpty.setCategory(category.getCname());
        return articleEsEmpty;
    }


    /**
     * 将html对象转换成文本对象
     * @param htmlStr
     * @return
     */
    private String delHTMLTag(String htmlStr){
        //定义script的正则表达式
        String regExScript ="<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式
        String regExStyle ="<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式
        String regExHtml ="<[^>]+>";

        Pattern pScript = Pattern.compile(regExScript ,Pattern.CASE_INSENSITIVE);
        Matcher mScript =pScript.matcher(htmlStr);
        //过滤script标签
        htmlStr=mScript .replaceAll("");

        Pattern pStyle =Pattern.compile(regExStyle ,Pattern.CASE_INSENSITIVE);
        Matcher mStyle =pStyle.matcher(htmlStr);
        //过滤style标签
        htmlStr=mStyle .replaceAll("");

        Pattern pHtml =Pattern.compile(regExHtml ,Pattern.CASE_INSENSITIVE);
        Matcher mHtml =pHtml.matcher(htmlStr);
        //过滤html标签
        htmlStr=mHtml.replaceAll("");
        //返回文本字符串
        return htmlStr.trim();
    }
}

