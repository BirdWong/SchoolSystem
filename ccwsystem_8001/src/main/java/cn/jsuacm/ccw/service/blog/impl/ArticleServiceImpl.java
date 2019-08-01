package cn.jsuacm.ccw.service.blog.impl;

import cn.jsuacm.ccw.mapper.blog.ArticleMapper;
import cn.jsuacm.ccw.pojo.blog.Article;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.blog.ArticleInfomationService;
import cn.jsuacm.ccw.service.blog.ArticleService;
import cn.jsuacm.ccw.service.blog.EsArticleService;
import cn.jsuacm.ccw.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @ClassName ArticleServiceImpl
 * @Description 文章接口实现类
 * @Author h4795
 * @Date 2019/06/18 22:21
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{

    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate template;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleInfomationService articleInfomationService;

    @Autowired
    private EsArticleService esArticleService;

    @Autowired
    private RedisUtil redisUtil;

    static String[]  MONTH = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};


    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/isUser/";

    /**
     * 添加用户的文章，kind类型必须为0
     * @param article 文章实体类
     * @return
     */
    @Override
    public MessageResult addUserArticle(Article article) {
        int uid = article.getUid();
        // 确认有这个用户
        ResponseEntity<String> entity = template.getForEntity(url + String.valueOf(uid), String.class);

        if (!"true".equals(entity.getBody())){
            return new MessageResult(false, "没有这个用户");
        }
        // 校验文章数据
        MessageResult messageResult = Article.checkArticle(article, Article.USER_ARTICLE);
        if (!messageResult.isStatus()){
            return messageResult;
        }

        article.setKind(Article.USER_ARTICLE);
        article.setCreateTime(new Date());
        article.setModifyTime(new Date());
        article.setViews(0);
        article.setUid(uid);
        boolean save = save(article);
        if (!save){
            throw new RuntimeException("文章保存出错");
        }
        // 返回自增主键
        return new MessageResult(true, String.valueOf(article.getAid()));
    }

    /**
     * 获取一篇文章的所有内容用于修改
     *
     * @param uid 用户id
     * @param aid 文章id
     * @return
     */
    @Override
    public Article getUpdateArticle(int uid, int aid) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("aid", aid);
        Article article = getOne(wrapper);
        return article;
    }

    /**
     * 更新文章
     *
     * @param uid     用户的id
     * @param article 更新的文章
     * @return
     */
    @Override
    public MessageResult updateUserArticle(int uid, Article article) {

        // 查看这篇文章是否还存在，或者是否属于此人
        Article oldArticle = getById(article.getAid());
        if (oldArticle == null || oldArticle.getUid() != uid){
            return new MessageResult(false, "文章id错误,或者这篇文章不属于此人");
        }

        // 校验文章
        MessageResult messageResult = Article.checkArticle(article, Article.USER_ARTICLE);
        if (!messageResult.isStatus()){
            return messageResult;
        }

        // 修改修改时间
        article.setModifyTime(new Date());
        // 修改文章时有可能也被访问，所以要以数据库的查看量为准
        article.setViews(oldArticle.getViews());
        updateById(article);
        esArticleService.save(article);
        return new MessageResult(true, "修改成功");
    }

    /**
     * 修改文章的状态
     *
     * @param uid    用户的id
     * @param aid    文章的id
     * @param status 文章的状态
     * @return
     */
    @Override
    public MessageResult changeStatus(int uid, int aid, int status) {

        Article article = getById(aid);
        if (article == null){
            return new MessageResult(false, "没有这篇文章");
        }
        if (!Article.STATUS_SET.contains(status)){
            return new MessageResult(false, "状态错误");
        }
        if (article.getUid() == uid){
            UpdateWrapper<Article> wrapper = new UpdateWrapper<>();
            wrapper.eq("aid", aid).set("status",status);
            update(wrapper);
            esArticleService.save(article);
            return new MessageResult(true, "修改成功");
        }else {
            return new MessageResult(false, "这篇文章不属于你");
        }

    }

    /**
     * 彻底删除一篇文章
     *
     * @param uid 用户id
     * @param aid 文章id
     * @return
     */
    @Override
    public MessageResult deleteArticle(int aid, int uid) {
        Article article = getById(aid);
        if (article == null || article.getUid() != uid){
            return new MessageResult(false, "这篇文章不存在或者不属于你");
        }else {
            articleInfomationService.deleteByAid(aid, uid);
            esArticleService.deleteById(aid);
            removeById(aid);
            return new MessageResult(true, "删除成功");
        }
    }

    /**
     * 查看一篇文章有多少浏览量
     *
     * @param aid 文章id
     * @return
     */
    @Override
    public MessageResult getView(int aid) {
        Article article = getById(aid);
        return article == null ? new MessageResult(false, "没有这篇文章") : new MessageResult(true, article.getViews()+"");
    }

    /**
     * 查看这个用户一共有多少浏览量
     *
     * @param uid
     * @return
     */
    @Override
    public MessageResult getViews(int uid) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        List<Article> articles = articleMapper.selectList(wrapper);
        if (articles.size() == 0){
            return new MessageResult(false, "没有这个用户的文章");
        }
        Long countViews = new Long(0);
        for (Article article : articles){
            countViews += article.getViews();
        }

        return new MessageResult(true, countViews.toString());
    }

    /**
     * 查看一篇文章，只允许查看用户的文章和发布的文章
     *
     * @param aid 文章id
     * @return
     */
    @Override
    public Article getUserArticle(int aid) {
        Article article = getById(aid);

        // 如果没有这篇文章
        if (article == null){
            return null;
        }

        // 如果文章的状态时私有状态不准未登录查看
        if (article.getStatus() != Article.PUBLIC_ARTICLE){
            return null;
        }

        // 如果这不是用户普通文章，不能通过这个方法查看
        if (article.getKind() != Article.USER_ARTICLE){
            return null;
        }

        // 增加查看量
        UpdateWrapper<Article> wrapper = new UpdateWrapper<>();
        wrapper.eq("aid", aid).set("views", article.getViews() + 1);
        update(wrapper);

        updateHots(article);

        // 返回文章
        return article;


    }

    /**
     * 更新热门文章
     * @param article
     */
    private void updateHots(Article article) {
        Map<Object, Object> hotArticle = null;
        synchronized (ArticleServiceImpl.class) {
            if (redisUtil.hasKey("hot_article")) {
                hotArticle = redisUtil.hmget("hot_article");
                Object articleOrDefault = hotArticle.getOrDefault(String.valueOf(article.getAid()), 0);
                hotArticle.put(String.valueOf(article.getAid()), Integer.valueOf(String.valueOf(articleOrDefault)) + 1);
                redisUtil.hmset("hot_article", hotArticle, redisUtil.getExpire("hot_article"));
            }else {
                hotArticle = new HashMap();
                hotArticle.put(article.getAid(), 1);
                redisUtil.hmset("hot_article", hotArticle, 24*60*60);
            }

        }
    }

    /**
     * 获取一个用户的文章的列表
     *
     * @param uid 用户的id
     * @return
     */
    @Override
    public PageResult<Article> getUserArticleList(int uid, int row, int pageSize) {

        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).orderByAsc("aid");
        return searchForAnyArticles(row, pageSize, wrapper);
    }

    /**
     * 获取一个用户的公开的文章列表
     *
     * @param uid
     * @param row
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<Article> getUserPublicArticleList(int uid, int row, int pageSize) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid)
                .eq("status", Article.PUBLIC_ARTICLE)
                .eq("kind", Article.USER_ARTICLE)
                .orderByAsc("aid");
        return searchForAnyArticles(row, pageSize, wrapper);
    }

    /**
     * 获取一个用户的私密的文章列表
     *
     * @param uid
     * @param row
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<Article> getUserPrivateArticleList(int uid, int row, int pageSize) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid)
                .eq("status", Article.PRIVETA_ARTICLE)
                .eq("kind", Article.USER_ARTICLE)
                .orderByAsc("aid");
        return searchForAnyArticles(row, pageSize, wrapper);
    }

    /**
     * 获取一个用户的草稿箱的文章列表
     *
     * @param uid
     * @param row
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<Article> getUserDraftArticleList(int uid, int row, int pageSize) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid)
                .eq("status", Article.DRAFT_ARTICLE)
                .eq("kind", Article.USER_ARTICLE)
                .orderByAsc("aid");
        return searchForAnyArticles(row, pageSize, wrapper);
    }


    /**
     * 按照定义好的条件搜索分页内容
     * @param row 当前页
     * @param pageSize 页面大小
     * @param queryWrapper 查询条件
     * @return
     */
    private PageResult<Article> searchForAnyArticles(int row, int pageSize, QueryWrapper<Article> queryWrapper){
        Page<Article> articlePage = new Page<>();
        articlePage.setCurrent(row);
        articlePage.setSize(pageSize);
        IPage<Article> articleIPage = articleMapper.selectPage(articlePage, queryWrapper);
        PageResult<Article> articlePageResult = new PageResult<>();
        articlePageResult.setTatolSize(articleIPage.getTotal());
        articlePageResult.setRow(articleIPage.getCurrent());
        articlePageResult.setPageSize(articleIPage.getSize());
        articlePageResult.setPageContext(articleIPage.getRecords());
        return articlePageResult;
    }

    /**
     * 获取用户发表的公开，用户个人的文章归档
     *
     * @param uid 用户的id
     * @return
     */
    @Override
    public TreeMap<String, LinkedList<Article>> getUserPublicArchive(int uid) {
        // 查询出所有的信息并按照时间排序
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid)
                .eq("status", Article.PUBLIC_ARTICLE)
                .eq("kind", Article.USER_ARTICLE)
                .orderByAsc("create_time");
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return sortArticleByDate(articles);


    }


    /**
     * 获取用户私密的文章归档
     * @param uid 用户的id
     * @return
     */
    @Override
    public TreeMap<String, LinkedList<Article>> getUserPrivateArchive(int uid) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid)
                .eq("status", Article.PRIVETA_ARTICLE)
                .eq("kind", Article.USER_ARTICLE).orderByAsc("create_time");
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return sortArticleByDate(articles);
    }

    /**
     * 获取用户的草稿文章列表
     *
     * @param uid 用户的id
     * @return
     */
    @Override
    public TreeMap<String, LinkedList<Article>> getUserDraftArchive(int uid) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid)
                .eq("status", Article.DRAFT_ARTICLE)
                .eq("kind", Article.USER_ARTICLE).orderByAsc("create_time");
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return sortArticleByDate(articles);
    }


    /**
     * 根据时间进行排序归档
     * @param articles
     * @return
     */
    private TreeMap<String, LinkedList<Article>> sortArticleByDate(List<Article> articles) {
        // 创建treeMap并定义排序规则
        TreeMap<String, LinkedList<Article>> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                String[] split1 = s.split("-");
                String[] split2 = t1.split("-");
                if (Integer.valueOf(split1[0]).compareTo(Integer.valueOf(split2[0])) != 0){
                    return Integer.valueOf(split1[0]).compareTo(Integer.valueOf(split2[0]));
                }

                return Integer.valueOf(split1[1]).compareTo(Integer.valueOf(split2[1]));
            }
        });


        // 将内容按照月份归档
        for (Article article : articles){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(article.getCreateTime());
            // 获取key值
            String key = calendar.get(Calendar.YEAR)+"-"+MONTH[calendar.get(Calendar.MONTH)];

            if (treeMap.containsKey(key)){
                LinkedList<Article> articleList = treeMap.get(key);
                Article tmpArticle = new Article();
                tmpArticle.setUid(article.getUid());
                tmpArticle.setTitle(article.getTitle());
                tmpArticle.setViews(article.getViews());
                articleList.add(tmpArticle);
                treeMap.put(key, articleList);
            }else {
                LinkedList<Article> articleList = new LinkedList<>();
                Article tmpArticle = new Article();
                tmpArticle.setUid(article.getUid());
                tmpArticle.setTitle(article.getTitle());
                tmpArticle.setViews(article.getViews());
                articleList.add(tmpArticle);
                treeMap.put(key, articleList);
            }
        }

        return treeMap;
    }



    /**
     * 获取用户最近发表的文章
     *
     * @param uid  用户的id
     * @param size 需要的文章的数量
     * @return
     */
    @Override
    public List<Article> getNewArticle(int uid, int size) {

        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).eq("status",Article.PUBLIC_ARTICLE).eq("kind", Article.USER_ARTICLE).orderByDesc("create_time").last(" limit "+size);
        List<Article> articles = articleMapper.selectList(wrapper);
        return articles;
    }



    /**
     * 分页获取24H内点击次数最高的文章
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public PageResult getHotsArticles(int current, int pageSize) {
        PageResult<Article> articlePageResult = new PageResult<>();
        articlePageResult.setRow((long)current);
        articlePageResult.setPageSize((long)pageSize);
        if (redisUtil.hasKey("hot_article")){

            Map<Object, Object> hot_article = redisUtil.hmget("hot_article");

            ArrayList<Map.Entry<Object,Object>> articleList = new ArrayList(hot_article.entrySet());
            Collections.sort(articleList, new Comparator<Map.Entry<Object, Object>>() {

                @Override
                public int compare(Map.Entry<Object, Object> objectObjectEntry, Map.Entry<Object, Object> t1) {
                    return Integer.valueOf(String.valueOf(t1.getValue())) - Integer.valueOf(String.valueOf(objectObjectEntry.getValue()));

                }
            });

            articlePageResult.setTatolSize((long)articleList.size());
            List<Map.Entry<Object, Object>> tmpList = articleList.subList(current * pageSize, (current + 1) * pageSize);
            articlePageResult.setPageContext(new ArrayList<>());
            for (int i = 0; i < tmpList.size(); i++){
                Article userArticle = getUserArticle(Integer.valueOf(String.valueOf(tmpList.get(i).getKey())));
                articlePageResult.getPageContext().add(userArticle);
            }
        }else {
            articlePageResult.setTatolSize(0L);

        }
            return articlePageResult;
    }

    /**
     * 删除用户类型的文章
     *
     * @param uid
     * @return
     */
    @Override
    public void deleteUserArticleByUid(int uid) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("aid").eq("uid", uid).eq("kind", Article.USER_ARTICLE);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        UpdateWrapper<Article> articleDeleteWrapper = new UpdateWrapper<>();
        articleDeleteWrapper.eq("uid", uid).eq("kind", Article.USER_ARTICLE);
        articleMapper.delete(articleDeleteWrapper);
        for (Article article: articles){
            esArticleService.deleteById(article.getAid());
        }
    }

    /**
     * 根据aid删除文章
     *
     * @param aid
     */
    @Override
    public void deleteByAid(int aid) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aid", aid);
        articleMapper.delete(queryWrapper);
        esArticleService.deleteById(aid);
    }


}
