package cn.jsuacm.gateway.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName User
 * @Description 用户对象，定义了用户id，用户账号，用户邮箱，用户昵称，用户密码，用户头像链接
 * @Author h4795
 * @Date 2019/06/17 16:23
 */
@TableName(value = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户对象")
public class User implements Serializable{

    /**
     * 用户id，自增长
     */
    @TableId(value = "uid", type = IdType.AUTO)
    @ApiModelProperty(value = "用户的id", dataType = "int")
    private int uid;

    /**
     * 用户账号，可用于登录（学号为主）
     */
    @ApiModelProperty(value = "用户的账号，以学号为主，注册后不可修改", required = true, dataType = "string")
    @TableField(strategy = FieldStrategy.NOT_NULL,value = "account_number")
    private String accountNumbser;


    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户绑定的邮箱", required = true, dataType = "string")
    @TableField(strategy = FieldStrategy.NOT_NULL, value = "email")
    private String email;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户的昵称", required = true, dataType = "string")
    @TableField(strategy = FieldStrategy.NOT_NULL, value = "username")
    private String username;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户的密码", required = true, dataType = "string")
    @TableField(strategy = FieldStrategy.NOT_NULL, value = "password")
    private String password;

    /**
     * 头像url
     */
    @ApiModelProperty(value = "用户的头像, 如果用户没有设置， 也就是查询出来的信息这个时空，请直接前端直接指向一个头像", required = false, dataType = "string")
    @TableField(value = "picture_url")
    private String prictureUrl;


}
