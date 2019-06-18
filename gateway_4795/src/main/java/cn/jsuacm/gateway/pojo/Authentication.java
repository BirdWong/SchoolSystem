package cn.jsuacm.gateway.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * @ClassName Authentication
 * @Description 权限实体类
 * @Author h4795
 * @Date 2019/06/17 21:37
 */
@TableName(value = "authentication")
@Data
public class Authentication {

    @TableId(value = "aid", type = IdType.AUTO)
    private int aid;

    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    private int uid;

    @TableField(value = "role", strategy = FieldStrategy.NOT_NULL)
    private String role;
}
