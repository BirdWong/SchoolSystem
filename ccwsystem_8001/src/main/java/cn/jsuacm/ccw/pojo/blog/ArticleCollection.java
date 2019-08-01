package cn.jsuacm.ccw.pojo.blog;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ArticleCollection
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 10:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "文章收藏对象， aid不是文章的id， 是文章信息表的id")
@TableName("article_collection")
public class ArticleCollection {

    /**
     * 收藏id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "收藏对象的id", dataType = "int")
    private int id;

    /**
     * 文章信息的id
     */
    @TableField(value = "aid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "文章信息的id", dataType = "int", required = true)
    private int aid;

    /**
     * 用户的id
     */
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "用户的id", dataType = "int", required = true)
    private int uid;

}
