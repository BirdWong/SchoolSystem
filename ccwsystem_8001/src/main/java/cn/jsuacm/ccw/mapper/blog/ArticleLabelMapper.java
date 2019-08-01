package cn.jsuacm.ccw.mapper.blog;

import cn.jsuacm.ccw.pojo.blog.ArticleLabel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ArticleLabelMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/26 10:19
 */
@Mapper
@Component
public interface ArticleLabelMapper extends BaseMapper<ArticleLabel>{

    /**
     * 通过lid查找对应的记录
     * @param lid 标签的id
     * @return 包含文章和标签信息的列表
     */
    @Select("select id, lid, aid from article_labels where uid=#{lid}")
    public List<ArticleLabel> queryByLid(@Param(value = "lid") int lid);


    /**
     * 通过aid查找对应的记录
     * @param aid aid
     * @return
     */
    @Select("select id, lid, aid from article_labels where aid=#{aid}")
    public List<ArticleLabel> queryByAid(@Param(value = "aid") int aid);

    /**
     * 通过文章id删除对应的记录
     * @param aid 文章id
     */
    @Delete("delete from article_labels where aid=#{aid}")
    public void deleteByAid(@Param(value = "aid") int aid);

    /**
     * 通过标签的id删除对应的记录
     * @param lid 标签的id
     */
    @Delete("delete from article_labels where lid=#{lid}")
    public void deleteByLid(@Param(value = "lid") int lid);
}
