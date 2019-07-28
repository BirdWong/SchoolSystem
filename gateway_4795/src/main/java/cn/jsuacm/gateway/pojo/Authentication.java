package cn.jsuacm.gateway.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName Authentication
 * @Description 权限实体类
 * @Author h4795
 * @Date 2019/06/17 21:37
 */
@TableName(value = "authentication")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户权限对象")
public class Authentication implements Serializable{

    @TableId(value = "aid", type = IdType.AUTO)
    @ApiModelProperty(value = "用户权限对象id", dataType = "int")
    private int aid;

    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "用户的id", required = true, dataType = "int")
    private int uid;

    @TableField(value = "role", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "权限名称", dataType = "string", required = true)
    private String role;
}
