package cn.jsuacm.ccw.service.impl;

import cn.jsuacm.ccw.mapper.*;
import cn.jsuacm.ccw.pojo.*;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.ArticleInfomationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @ClassName ArticleInfomationServiceImpl
 * @Description articleinfomation服务层实现类
 * @Author h4795
 * @Date 2019/06/23 17:24
 */
@Service
public class ArticleInfomationServiceImpl extends ServiceImpl<ArticleInfomationMapper, ArticleInfomation> implements ArticleInfomationService {

    @Autowired
    private ArticleServiceImpl articleService;


    @Autowired
    private LabelMapper labelMapper;


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ArticleInfomationMapper articleInfomationMapper;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ArticleLabelMapper articleLabelMapper;

    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/isUser/";
    /**
     * 添加一个文章的信息
     *
     * @param articleInfomation
     * @return
     */
    @Override
    public MessageResult add(ArticleInfomation articleInfomation) {


        // 检查是否符合规范
        boolean appertaining = checkAppertaining(articleInfomation.getUid(), articleInfomation.getAid(), articleInfomation.getCid(), articleInfomation.getLids());
        if (appertaining){
            save(articleInfomation);
            for (ArticleLabel articleLabel : articleInfomation.getLids()){
                articleLabelMapper.insert(articleLabel);
            }
            return new MessageResult(true, "保存成功");
        }else {
            return new MessageResult(false, "请检查信息从属关系或确认是否存在");
        }

    }


    /**
     * 删除一个文章的信息
     * @param uid 用户id
     * @param id 信息id
     * @return
     */
    @Override
    public MessageResult deleteById(int uid, int id) {
        ArticleInfomation articleInfomation = getById(id);
        if (articleInfomation.getUid() == uid){
            removeById(id);
            articleLabelMapper.deleteByAid(articleInfomation.getAid());
            return new MessageResult(true, "删除成功");
        }else {
            return new MessageResult(false, "删除失败");
        }
    }

    /**
     * 通过文章id删除一个文章信息
     * @param aid 文章id
     * @return
     */
    @Override
    public MessageResult deleteByAid(int aid) {
        UpdateWrapper<ArticleInfomation> wrapper = new UpdateWrapper<>();
        wrapper.eq("aid", aid);
        articleInfomationMapper.delete(wrapper);
        articleLabelMapper.deleteByAid(aid);
        return new MessageResult(true, "删除成功");
    }

    /**
     * 通过一个用户的id删除信息
     * 删除内容
     * - 文章表uid等于传入的参数的内容
     * - kind 等于 Article::USER_ARTICLE
     * - kind 不等于Article::USER_ARTICLE， 将uid修改到超级管理员下
     * - 该用户创建的所有的标签
     * - 文章信息表中uid属性等于传入参数的
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public MessageResult deleteAllByUid(int uid) {
        // 删除文章信息表中的信息
        QueryWrapper<ArticleInfomation> articleInfomationQueryWrapper = new QueryWrapper<>();
        articleInfomationQueryWrapper.select("aid").select("id").eq("uid", uid);
        List<ArticleInfomation> articleInfomations = articleInfomationMapper.selectList(articleInfomationQueryWrapper);
        for (ArticleInfomation articleInfomation : articleInfomations){
            removeById(articleInfomation.getId());
            articleLabelMapper.deleteByAid(articleInfomation.getAid());
        }
        // 删除用户属性的文章

        // 修改非用户属性文章归属
        UpdateWrapper<Article> articleUpdateWrapper = new UpdateWrapper<>();
        articleUpdateWrapper.eq("uid",uid);
        Article article = new Article();
        article.setUid(1);
        articleService.deleteUserArticleByUid(uid);
        // 删除这个用户的所有标签
        UpdateWrapper<Label> labelDeleteWrapper =  new UpdateWrapper<>();
        labelDeleteWrapper.eq("uid", uid);
        labelMapper.delete(labelDeleteWrapper);
        return new MessageResult(true, "删除完成");
    }

    /**
     * 通过一个标签id删除文章信息
     * 删除内容
     * - 文章信息表中lids出现了这个id内容
     * - 所属这个标签的所有文章
     *
     * @param lid 标签id
     * @return
     */
    @Override
    public MessageResult deleteAllByLid(int uid, int lid) {

        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("lid", lid).select("uid");
        Integer count = labelMapper.selectCount(labelQueryWrapper);
        if (count == 0){
            return new MessageResult(false, "该标签不属于此用户");
        }
        // 查找标签对应的文章
        List<ArticleLabel> articleLabels = articleLabelMapper.queryByLid(lid);
        articleLabelMapper.deleteByLid(lid);
        Set<Integer> aidSet = new HashSet<>();
        for (ArticleLabel articleLabel : articleLabels){
            if (!aidSet.contains(articleLabel.getAid())){
                // 根据aid删除文章信息
                deleteByAid(articleLabel.getAid());
                // 删除文章
                articleService.deleteByAid(articleLabel.getAid());
                // 把删除过的aid保存，防止重复删除
                aidSet.add(articleLabel.getAid());
            }

        }

        return new MessageResult(true, "删除完成");
    }

    /**
     * 通过一个二级分类id删除文章信息
     * 删除内容
     * - 文章信息表中cid出现了这个参数内容
     * - 所属这个cid的所有文章
     * @param cid 分类id
     * @return
     */
    @Override
    public MessageResult deleteAllBySencondCid( int cid) {
        // 1. 查询出所有符合条件的文章信息，查询内容 aid， id
        QueryWrapper<ArticleInfomation> articleInfomationQueryWrapper = new QueryWrapper<>();
        articleInfomationQueryWrapper.select("id", "aid").eq("cid", cid);
        List<ArticleInfomation> articleInfomations = articleInfomationMapper.selectList(articleInfomationQueryWrapper);

        // 2. 删除符合条件的文章信息和文章
        for (ArticleInfomation articleInfomation : articleInfomations){
            // 删除文章信息
            removeById(articleInfomation.getId());

            // 删除文章标签信息
            articleLabelMapper.deleteByAid(articleInfomation.getAid());

            // 删除文章
            articleService.deleteByAid(articleInfomation.getAid());
        }


        return new MessageResult(true, "删除完成");

    }


    /**
     * 通过用户的id查询他的所有文章信息
     * @param status 文章的状态
     * @param uid 用户的id
     * @return
     */
    @Override
    public List<ArticleInfomation> getByUid(int uid, int status) {
        List<ArticleInfomation> articleInfomations = articleInfomationMapper.getByUid(uid, status);
        for (ArticleInfomation articleInfomation : articleInfomations){
            articleInfomation.setLids(articleLabelMapper.queryByAid(articleInfomation.getAid()));
        }
        return articleInfomations;
    }

    /**
     * 通过文章id和文章状态查询这个文章信息
     * @param status 文章状态
     * @param aid 文章id
     * @return
     */
    @Override
    public ArticleInfomation getByAid(int aid, int status) {
        ArticleInfomation articleInfomation = articleInfomationMapper.getByAid(aid, status);
        articleInfomation.setLids(articleLabelMapper.queryByAid(aid));
        return articleInfomation;
    }


    /**
     * 通过标签查询所属文章信息
     * @param status 文章信息
     * @param lid 标签id
     * @return
     */
    @Override
    public List<ArticleInfomation> getByLid(int lid, int status) {
        // 查询与这个标签相关的所有文章信息
        List<ArticleLabel> articleLabels = articleLabelMapper.queryByLid(lid);

        List<ArticleInfomation> articleInfomations = new ArrayList<>();
        // 用于记录aid 防止重复操作
        Set<Integer> aidSet = new HashSet<>();
        for (ArticleLabel articleLabel : articleLabels){
            // 如果没有找到这个aid 说明没有被操作过
            if (!aidSet.contains(articleLabel.getAid())){
                aidSet.add(articleLabel.getAid());
                ArticleInfomation articleInfomation = getByAid(articleLabel.getAid(), status);
                articleInfomations.add(articleInfomation);
            }
        }
        return articleInfomations;
    }

    /**
     * 通过一个二级分类id查询文章信息
     * @param cid 二级分类id
     * @param status 文章的状态
     * @return
     */
    @Override
    public List<ArticleInfomation> getByCid(int cid, int status) {
        List<ArticleInfomation> articleInfomations = articleInfomationMapper.getByCid(cid, status);
        for (ArticleInfomation articleInfomation : articleInfomations){
            List<ArticleLabel> articleLabels = articleLabelMapper.queryByAid(articleInfomation.getAid());
            articleInfomation.setLids(articleLabels);
        }
        return articleInfomations;
    }

    /**
     * 修改文章属性
     *
     * @param articleInfomation
     * @return
     */
    @Override
    public MessageResult update(ArticleInfomation articleInfomation) {

        boolean appertaining = checkAppertaining(articleInfomation.getUid(), articleInfomation.getAid(), articleInfomation.getCid(), articleInfomation.getLids());
        if (appertaining){
            ArticleInfomation oldArticleInfomation = getById(articleInfomation.getId());
            if (oldArticleInfomation == null || oldArticleInfomation.getUid() != articleInfomation.getUid()){
                return new MessageResult(false, "请检查信息从属关系或确认是否存在");
            }
            oldArticleInfomation.setLids(articleLabelMapper.queryByAid(oldArticleInfomation.getAid()));

            // 判断有哪些标签被更改
            Set<Integer> lids = new HashSet<>();
            // 设置原有的标签
            for (ArticleLabel articleLabel : articleInfomation.getLids()){
                lids.add(articleLabel.getLid());
            }

            //
            for (ArticleLabel articleLabel : oldArticleInfomation.getLids()){
                // 如果原来就有这个标签， 就不用删除或者增加， 所以可以从list和set中移除
                if (lids.contains(articleLabel.getLid())){
                    lids.remove(articleLabel.getLid());
                    oldArticleInfomation.getLids().remove(articleLabel);
                }
            }

            // 从list中移除过时的标签
            for (ArticleLabel articleLabel : oldArticleInfomation.getLids()){
                articleLabelMapper.deleteById(articleLabel.getId());
            }

            // 设置新的标签
            for (Integer lid : lids){
                ArticleLabel articleLabel = new ArticleLabel();
                articleLabel.setLid(lid);
                articleLabel.setAid(articleInfomation.getAid());
                articleLabelMapper.insert(articleLabel);
            }
            // 更新文章信息
            update(articleInfomation);
            return new MessageResult(true, "修改成功");
        }else {
            return new MessageResult(false, "请检查信息从属关系或确认是否存在");    
        }
    }


    /**
     * 校验一篇文章的信息、从属关系
     * @param uid 用户的id
     * @param aid 文章的id
     * @param cid 二级分类的id
     * @param lids 标签列表字符串
     * @return
     */
    protected boolean checkAppertaining(int uid, int aid, int cid, List<ArticleLabel> lids){

        // 如果上述任何一个信息没有，直接异常
        if(uid ==0 || aid == 0 || cid == 0 || lids.size() > 5){
            return false;
        }
        // 确认有这个用户
        ResponseEntity<String> entity = restTemplate.getForEntity(url + String.valueOf(uid), String.class);

        if (!"true".equals(entity.getBody())){
            return false;
        }


        // 验证这篇文章是否是这个用户的
        QueryWrapper<Article> articleWrapper = new QueryWrapper<>();

        articleWrapper.eq("aid", aid);

        articleWrapper.select("uid");

        Article article = articleService.getOne(articleWrapper);
        // 如果没有这篇文章
        if (article == null){
            return false;
        }

        // 如果不是这个用户的
        if (uid != article.getUid()){
            return false;
        }

        // 查询使用的标签是否是这个用户的
        QueryWrapper<Label> labelWrapper = new QueryWrapper<>();
        int[] labelIds = new int[lids.size()];
        int index  = 0;
        for (ArticleLabel articleLabel : lids){
            labelIds[index++] = articleLabel.getLid();
        }
        labelWrapper.eq("uid",uid).in("lid", lids);

        Integer size = labelMapper.selectCount(labelWrapper);
        if (size != index){
            return false;
        }

        // 查询是否有这个分类
        QueryWrapper<Category> categoryWrapper = new QueryWrapper<>();
        categoryWrapper.eq("cid", cid).ne("parent_id", 0);
        Integer count = categoryMapper.selectCount(categoryWrapper);
        if (count == 0){
            return false;
        }

        return true;

    }

}
