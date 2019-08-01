package cn.jsuacm.ccw.mapper.blog;

import cn.jsuacm.ccw.pojo.blog.UserFocus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserFocusMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/28 21:05
 */
@Mapper
@Component
public interface UserFocusMapper extends BaseMapper<UserFocus>{
}
