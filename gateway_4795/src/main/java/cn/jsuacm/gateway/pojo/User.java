package cn.jsuacm.gateway.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * @ClassName User
 * @Description 用户对象，定义了用户id，用户账号，用户邮箱，用户昵称，用户密码，用户头像链接
 * @Author h4795
 * @Date 2019/06/17 16:23
 */
@TableName(value = "user")
@Data
public class User {

    /**
     * 用户id，自增长
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private int uid;

    /**
     * 用户账号，可用于登录（学号为主）
     */
    @TableField(strategy = FieldStrategy.NOT_NULL,value = "account_number")
    private String accountNumbser;


    /**
     * 用户邮箱
     */
    @TableField(strategy = FieldStrategy.NOT_NULL, value = "email")
    private String email;

    /**
     * 用户昵称
     */
    @TableField(strategy = FieldStrategy.NOT_NULL, value = "username")
    private String username;

    /**
     * 用户密码
     */
    @TableField(strategy = FieldStrategy.NOT_NULL, value = "password")
    private String password;

    /**
     * 头像url
     */
    @TableField(value = "picture_url")
    private String prictureUrl;


}
