package cn.jsuacm.gateway.mapper;

import cn.jsuacm.gateway.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserMapper
 * @Description 用户的mapper操作
 * @Author h4795
 * @Date 2019/06/17 16:27
 */
@Mapper
@Component
public interface UserMapper extends BaseMapper<User>{
}
