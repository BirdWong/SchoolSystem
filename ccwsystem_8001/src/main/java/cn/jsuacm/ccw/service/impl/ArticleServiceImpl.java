package cn.jsuacm.ccw.service.impl;

import cn.jsuacm.ccw.mapper.ArticleMapper;
import cn.jsuacm.ccw.pojo.Article;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
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
    private RestTemplate template;

    @Autowired
    private ArticleMapper articleMapper;


    static String[]  MONTH = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};


    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/isUser/";

    /**
     * 添加用户的文章，kind类型必须为0
     * @param uid     用户的id
     * @param article 文章实体类
     * @return
     */
    @Override
    public MessageResult addUserArticle(int uid, Article article) {
        // 确认有这个用户

        ResponseEntity<String> entity = template.getForEntity(url + String.valueOf(uid), String.class);

        if (!"true".equals(entity.getBody())){
            return new MessageResult(false, "没有这个用户");
        }
        // 校验文章数据
        MessageResult messageResult = checkArticle(article, Article.USER_ARTICLE);
        if (!messageResult.isStatus()){
            return messageResult;
        }

        article.setCreateTime(new Date());
        article.setModifyTime(new Date());
        article.setViews(0);
        article.setUid(uid);
        save(article);
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
        MessageResult messageResult = checkArticle(article, Article.USER_ARTICLE);
        if (!messageResult.isStatus()){
            return messageResult;
        }

        // 修改修改时间
        article.setModifyTime(new Date());
        // 修改文章时有可能也被访问，所以要以数据库的查看量为准
        article.setViews(oldArticle.getViews());
        updateById(article);
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
    public MessageResult deleteArticle(int uid, int aid) {
        Article article = getById(aid);
        if (article == null || article.getUid() != uid){
            return new MessageResult(false, "这篇文章不存在或者不属于你");
        }else {
            removeById(aid);
            return new MessageResult(true, "删除成功");
        }
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
        if (article.getStatus() == Article.PRIVETA_ARTICLE){
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
        // 返回文章
        return article;


    }

    /**
     * 获取一个用户的文章的列表
     *
     * @param uid 用户的id
     * @return
     */
    @Override
    public PageResult<Article> getUserArticleList(int uid, int row, int pageSize) {

        Page<Article> articlePage = new Page<>();
        articlePage.setPages(row);
        articlePage.setSize(pageSize);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid).orderByAsc("aid");
        IPage<Article> articleIPage = articleMapper.selectPage(articlePage, wrapper);
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
     * @param uid
     * @return
     */
    @Override
    public TreeMap<String, LinkedList<Article>> getUserArchive(int uid) {


        // 查询出所有的信息并按照时间排序
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid).orderByAsc("create_time");
        List<Article> articles = articleMapper.selectList(queryWrapper);

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
            // 排除非公开状态或者不是私人文章
            if (article.getStatus() == Article.PRIVETA_ARTICLE || article.getKind() != Article.USER_ARTICLE){
                continue;
            }

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
     * 校验文章格式等是否出现问题
     * @param article
     * @param kind
     * @return
     */
    public static MessageResult checkArticle(Article article, int kind){
        if (article.getTitle() == null || article.getTitle().length() == 0 || article.getTitle().length() < 101){
            return new MessageResult(false, "请正确填写标题，并且长度不能超过100");
        }
        if (article.getKind() != kind){
            return new MessageResult(false, "权限不足");
        }
        if (!Article.STATUS_SET.contains(article.getStatus())){
            return new MessageResult(false, "文章状态出现异常");
        }
        if (article.getContext() == null || article.getContext().length() == 0 || article.getHtmlContext() == null || article.getHtmlContext().length() == 0){
            return new MessageResult(false, "请填写文章内容");
        }
        return new MessageResult(true, "校验成功");
    }


}
