package cn.jsuacm.ccw.pojo.blog;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName Label
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/22 16:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "label")
@ApiModel(value = "文章标签")
public class Label implements Serializable {

    /**
     * ID
     */
    @TableId(value = "lid", type = IdType.AUTO)
    @ApiModelProperty(value = "标签id", dataType = "int")
    private int lid;


    /**
     * 标签名称
     */
    @TableField(value = "lname", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "标签名称", required = true, dataType = "string")
    private String lname;


    /**
     * 用户的id
     */
    @ApiModelProperty(value = "用户的id", required = true, dataType = "int")
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    private int uid;
}
