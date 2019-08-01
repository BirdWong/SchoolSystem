package cn.jsuacm.ccw.pojo.blog;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName Category
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/21 17:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "文章分类")
public class Category implements Serializable {

    /**
     * 分类id
     */
    @TableId(value = "cid", type = IdType.AUTO)
    @ApiModelProperty(value = "分类id", dataType = "int")
    private int cid;


    /**
     * 分类父分类id
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value = "文章父类id", required = true, dataType = "int")
    private int parentId;

    /**
     * 分类名称
     */
    @TableField(value = "cname")
    @ApiModelProperty(value = "分类的名称", required = true, dataType = "string")
    private String cname;
}
