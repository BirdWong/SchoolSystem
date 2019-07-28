package cn.jsuacm.ccw.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ArticleLabel
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/26 10:14
 */
@Data
@NoArgsConstructor
@TableName(value = "article_labels")
public class ArticleLabel {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "标识id")
    private int id;


    /**
     * 文章id
     */
    @ApiModelProperty(value = "文章id")
    @TableField(value = "aid", strategy = FieldStrategy.NOT_NULL)
    private int aid;

    /**
     * 标签id
     */
    @ApiModelProperty(value = "标签的id")
    @TableField(value = "lid", strategy = FieldStrategy.NOT_NULL)
    private int lid;


}
