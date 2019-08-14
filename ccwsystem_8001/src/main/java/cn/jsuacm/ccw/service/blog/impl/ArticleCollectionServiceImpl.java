package cn.jsuacm.ccw.service.blog.impl;

import cn.jsuacm.ccw.mapper.blog.ArticleCollectionMapper;
import cn.jsuacm.ccw.mapper.blog.ArticleInfomationMapper;
import cn.jsuacm.ccw.pojo.blog.ArticleCollection;
import cn.jsuacm.ccw.pojo.blog.ArticleInfomation;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.blog.ArticleCollectionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ArticleCollectionServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 11:00
 */
@Service
public class ArticleCollectionServiceImpl extends ServiceImpl<ArticleCollectionMapper, ArticleCollection> implements ArticleCollectionService {


    @Autowired
    private ArticleCollectionMapper articleCollectionMapper;


    @Autowired
    private ArticleInfomationMapper articleInfomationMapper;
    /**
     * 添加一个文章收藏
     * 验证是否有这个文章信息
     * 验证是否已经收藏
     * 保存信息
     * @param articleCollection
     * @return
     */
    @Override
    public MessageResult addCollection(ArticleCollection articleCollection) {

        QueryWrapper<ArticleInfomation> articleInfomationQueryWrapper = new QueryWrapper<>();
        articleInfomationQueryWrapper.eq("id", articleCollection.getAid());
        Integer countArticleInfomation = articleInfomationMapper.selectCount(articleInfomationQueryWrapper);
        if (countArticleInfomation == 0){
            return new MessageResult(false, "没有这个文章信息");
        }
        QueryWrapper<ArticleCollection> articleCollectionQueryWrapper = new QueryWrapper<>();
        articleCollectionQueryWrapper.eq("aid", articleCollection.getAid()).eq("uid", articleCollection.getUid());
        Integer countArticleCollection = articleCollectionMapper.selectCount(articleCollectionQueryWrapper);
        if (countArticleCollection != 0){
            return new MessageResult(false, "文章已经被收藏");
        }

        save(articleCollection);

        return new MessageResult(true, "收藏成功");

    }

    /**
     * 通过id删除一个收藏记录
     *
     * @param id
     * @return
     */
    @Override
    public MessageResult deleteById(int id, int uid) {
        ArticleCollection articleCollection = getById(id);
        if (articleCollection.getUid() != uid){
            return  new MessageResult(false, "这条信息不是此用户的");
        }
        removeById(id);
        return new MessageResult(true, "收藏记录删除成功");
    }

    /**
     * 根据一篇文章信息id删除， 用于删除文章时的操作
     *
     * @param aid 文章信息id
     * @return
     */
    @Override
    public MessageResult deleteByAid(int aid, int uid) {
        // 确认这个文章是属于这个用户的
        QueryWrapper<ArticleInfomation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aid", aid);
        ArticleInfomation articleInfomation = articleInfomationMapper.selectOne(queryWrapper);
        if (articleInfomation != null && articleInfomation.getUid() != uid){
            return new MessageResult(false, "这篇文章不属于这个用户");
        }
        UpdateWrapper<ArticleCollection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("aid", aid);
        articleCollectionMapper.delete(updateWrapper);
        return new MessageResult(true, "该文章的收藏删除成功");

    }

    /**
     * 通过用户id删除一篇文章  删除用户时调用
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public MessageResult deleteByUid(int uid) {
        UpdateWrapper<ArticleCollection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        articleCollectionMapper.delete(updateWrapper);
        return new MessageResult(true, "该用户的所有收藏删除成功");
    }

    /**
     * 分页获取一个用户的收藏列表
     *
     * @param uid      用户id
     * @param current  当前页
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public PageResult<ArticleCollection> getByUid(int uid, int current, int pageSize) {

        Page<ArticleCollection> articleCollectionPage = new Page<>();
        articleCollectionPage.setSize(pageSize);
        articleCollectionPage.setCurrent(current);

        QueryWrapper<ArticleCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        IPage<ArticleCollection> articleCollectionIPage = articleCollectionMapper.selectPage(articleCollectionPage, queryWrapper);
        PageResult<ArticleCollection> result = new PageResult<>();
        result.setTatolSize(articleCollectionIPage.getTotal());
        result.setPageContext(articleCollectionIPage.getRecords());
        result.setRow(articleCollectionIPage.getCurrent());
        result.setPageSize(articleCollectionIPage.getSize());
        return result;
    }

    /**
     * 通过文章信息id获取被收藏的分页列表
     *
     * @param aid      文章信息id
     * @param current  当前页
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public PageResult<ArticleCollection> getByAid(int aid, int uid, int current, int pageSize) {
        // 确认这篇文章是这个用户的
        // 确认这个文章是属于这个用户的
        QueryWrapper<ArticleInfomation> articleInfomationQueryWrapper = new QueryWrapper<>();
        articleInfomationQueryWrapper.eq("uid", uid);
        ArticleInfomation articleInfomation = articleInfomationMapper.selectOne(articleInfomationQueryWrapper);
        if (articleInfomation != null && articleInfomation.getUid() != uid){
            return new PageResult<>();
        }


        Page<ArticleCollection> articleCollectionPage = new Page<>();
        articleCollectionPage.setSize(pageSize);
        articleCollectionPage.setCurrent(current);

        QueryWrapper<ArticleCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("aid", aid);
        IPage<ArticleCollection> articleCollectionIPage = articleCollectionMapper.selectPage(articleCollectionPage, queryWrapper);
        PageResult<ArticleCollection> result = new PageResult<>();
        result.setTatolSize(articleCollectionIPage.getTotal());
        result.setPageContext(articleCollectionIPage.getRecords());
        result.setRow(articleCollectionIPage.getCurrent());
        result.setPageSize(articleCollectionIPage.getSize());

        return result;
    }
}
