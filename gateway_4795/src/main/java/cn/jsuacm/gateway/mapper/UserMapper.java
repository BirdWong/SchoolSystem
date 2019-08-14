package cn.jsuacm.gateway.mapper;

import cn.jsuacm.gateway.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Description 用户的mapper操作
 * @Author h4795
 * @Date 2019/06/17 16:27
 */
@Mapper
@Component
public interface UserMapper extends BaseMapper<User>{

    @Select("select u.uid, u.account_number, u.username, u.picture_url " +
            "from user u, authentication a " +
            "where u.uid=a.uid and a.role='ROLE_MENBER'")
    public List<User> getCcwMenber();

    @Select("select u.uid, u.account_number, u.username, u.picture_url " +
            "from user u, authentication a " +
            "where u.uid=a.uid and a.role='ROLE_ING'")
    public List<User> getCcwIngMenber();
}
