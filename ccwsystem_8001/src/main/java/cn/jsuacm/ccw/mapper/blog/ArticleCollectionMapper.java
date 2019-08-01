package cn.jsuacm.ccw.mapper.blog;

import cn.jsuacm.ccw.pojo.blog.ArticleCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName ArticleCollectionMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 10:58
 */
@Mapper
@Component
public interface ArticleCollectionMapper extends BaseMapper<ArticleCollection>{
}
