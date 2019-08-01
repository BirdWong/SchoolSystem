package cn.jsuacm.ccw.service.blog;

import cn.jsuacm.ccw.pojo.blog.ArticleCollection;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @ClassName ArticleCollectionService
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 10:59
 */
public interface ArticleCollectionService extends IService<ArticleCollection>{

    /**
     * 添加一个文章收藏
     *  验证是否有这个文章信息
     *  验证是否已经收藏
     *  保存信息
     * @param articleCollection
     * @return
     */
    public MessageResult addCollection(ArticleCollection articleCollection);


    /**
     * 通过id删除一个收藏记录
     * @param id
     * @return
     */
    public MessageResult deleteById(int id, int uid);


    /**
     * 根据一篇文章信息id删除， 用于删除文章时的操作
     * @param aid 文章信息id
     * @return
     */
    public MessageResult deleteByAid(int aid, int uid);


    /**
     * 通过用户id删除一篇文章  删除用户时调用
     * @param uid 用户id
     * @return
     */
    public MessageResult deleteByUid(int uid);


    /**
     * 分页获取一个用户的收藏列表
     * @param uid 用户id
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<ArticleCollection> getByUid(int uid, int current, int pageSize);


    /**
     * 通过文章信息id获取被收藏的分页列表
     * @param aid 文章信息id
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<ArticleCollection> getByAid(int aid, int uid, int current, int pageSize);

}
