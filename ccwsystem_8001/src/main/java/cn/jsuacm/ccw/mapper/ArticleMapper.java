package cn.jsuacm.ccw.mapper;

import cn.jsuacm.ccw.pojo.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName ArticleMapper
 * @Description 文章的mapper操作
 * @Author h4795
 * @Date 2019/06/18 22:18
 */
@Component
@Mapper
public interface ArticleMapper extends BaseMapper<Article>{

}
