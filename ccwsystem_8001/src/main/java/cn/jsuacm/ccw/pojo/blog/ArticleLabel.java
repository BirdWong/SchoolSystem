package cn.jsuacm.ccw.pojo.blog;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName ArticleLabel
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/26 10:14
 */
@Data
@NoArgsConstructor
@ApiModel(value = "文章和标签关系实体")
@TableName(value = "article_labels")
public class ArticleLabel implements Serializable{

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "标识id", dataType = "int")
    private int id;


    /**
     * 文章id
     */
    @ApiModelProperty(value = "文章id", dataType = "int")
    @TableField(value = "aid", strategy = FieldStrategy.NOT_NULL)
    private int aid;

    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签的id", dataType = "int")
    @TableField(value = "lid", strategy = FieldStrategy.NOT_NULL)
    private int lid;


}
