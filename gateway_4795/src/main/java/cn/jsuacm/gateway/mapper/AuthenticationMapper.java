package cn.jsuacm.gateway.mapper;

import cn.jsuacm.gateway.pojo.Authentication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName AuthenticationMapper
 * @Description 权限的mapper操作类
 * @Author h4795
 * @Date 2019/06/17 21:39
 */
@Mapper
@Component
public interface AuthenticationMapper extends BaseMapper<Authentication>{
}
