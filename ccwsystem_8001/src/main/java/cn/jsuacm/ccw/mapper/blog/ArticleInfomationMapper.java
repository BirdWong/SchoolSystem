package cn.jsuacm.ccw.mapper.blog;

import cn.jsuacm.ccw.pojo.blog.ArticleInfomation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ArticleInfomationMapper
 * @Description 文章信息的Mapper操作
 * @Author h4795
 * @Date 2019/06/23 16:27
 */
@Mapper
@Component
public interface ArticleInfomationMapper extends BaseMapper<ArticleInfomation>{


    /**
     * 通过文章的id和指定的状态获取文章信息
     * @param aid 文章id
     * @param status 文章状态
     * @return
     */
    @Select("select ai.id as id, ai.aid as aid, ai.cid as cid, ai.uid as uid from article_infomation ai, article a where  ai.aid=a.aid and ai.aid=#{aid} and a.kind=0 and a.status=#{status}")
    public ArticleInfomation getByAid(@Param("aid") int aid, @Param("status") int status);


    /**
     * 通过分类的id和文章的状态获取文章的信息列表
     * @param cid 分类的id
     * @param status 文章的状态
     * @return
     */
    @Select("select ai.id as id, ai.aid as aid, ai.cid as cid, ai.uid as uid from article_infomation ai, article a where  ai.aid=a.aid and ai.cid=#{cid} and a.kind=0 and a.status=#{status}")
    public List<ArticleInfomation> getByCid(@Param("cid") int cid, @Param("status") int status);


    /**
     * 通过用户的id和文章的状态获取文章的信息列表
     * @param uid 用户的id
     * @param status 文章的状态
     * @return
     */
    @Select("select ai.id as id, ai.aid as aid, ai.cid as cid, ai.uid as uid from article_infomation ai, article a where  ai.aid=a.aid and ai.uid=#{cid} and a.kind=0 and a.status=#{status}")
    public List<ArticleInfomation> getByUid(@Param("uid") int uid, @Param("status") int status);


}
