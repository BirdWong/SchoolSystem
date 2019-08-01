package cn.jsuacm.ccw.service.blog;

import cn.jsuacm.ccw.pojo.blog.ArticleComplex;
import cn.jsuacm.ccw.pojo.blog.ArticleInfomation;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @ClassName ArticleInfomationService
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/23 16:33
 */
public interface ArticleInfomationService extends IService<ArticleInfomation>{

    /**
     * 添加一个文章的信息
     * @param articleComplex 一个文章综合信息实体
     * @return
     */
    public MessageResult add(ArticleComplex articleComplex);

    /**
     * 删除通过自身id一个文章的信息
     * @param uid 用户id
     * @param id 信息id
     * @return
     */
    public MessageResult deleteById(int uid, int id);


    /**
     * 通过文章id删除一个文章信息
     * @param aid
     * @return
     */
    public MessageResult deleteByAid(int aid, int uid);


    /**
     * 管理员通过文章id删除一个文章信息
     * @param aid
     * @return
     */
    public MessageResult adminDeleteByAid(int aid);


    /**
     * 通过一个用户的id删除信息
     *  删除内容
     *      - 文章表uid等于传入的参数的内容
     *              - kind 等于 Article::USER_ARTICLE
     *      - 该用户创建的所有的标签
     *      - 文章信息表中uid属性等于传入参数的
     * @param uid 用户id
     * @return
     */
    public MessageResult deleteAllByUid(int uid);


    /**
     * 通过一个标签id删除文章信息
     * 删除内容
     *     - 文章信息表中lids出现了这个id内容
     *     - 所属这个标签的所有文章
     * @param lid 标签id
     * @param uid 用户id
     * @return
     */
    public MessageResult deleteAllByLid(int uid, int lid);


    /**
     * 通过一个二级分类id删除文章信息
     * 删除内容
     *     - 文章信息表中cid出现了这个参数内容
     *     - 所属这个cid的所有文章
     * @param cid 分类id
     * @return
     */
    public MessageResult deleteAllBySencondCid(int cid);


    /**
     * 通过用户的id查询他的所有文章信息
     * @param uid 用户id
     * @param status 文章状态
     * @return
     */
    public List<ArticleInfomation> getByUid(int uid, int status);


    /**
     * 通过文章id和文章状态查询这个文章信息
     * @param aid 文章id
     * @param status 文章状态
     * @return
     */
    public ArticleInfomation getByAid(int aid, int status);





    /**
     * 通过标签查询所属文章信息
     * @param lid 标签id
     * @param status  文章状态
     * @return
     */
    public List<ArticleInfomation> getByLid(int lid, int status);


    /**
     * 通过一个二级分类id查询文章信息
     * @param cid 二级分类的id
     * @param status 文章的状态
     * @return
     */
    public List<ArticleInfomation> getByCid(int cid, int status);


    /**
     * 修改文章属性
     * @param articleInfomation
     * @return
     */
    public MessageResult update(ArticleInfomation articleInfomation);

}
