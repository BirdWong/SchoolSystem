package cn.jsuacm.ccw.mapper;

import cn.jsuacm.ccw.pojo.Category;
import cn.jsuacm.ccw.pojo.Label;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName LabelMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/22 16:10
 */
@Mapper
@Component
public interface LabelMapper extends BaseMapper<Label>{
}
