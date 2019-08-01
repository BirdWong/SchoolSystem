package cn.jsuacm.ccw.pojo.blog;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ArticleInfomationMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/23 16:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "文章信息")
public class ArticleInfomation implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "文章信息表中的id", dataType = "int")
    @TableId(value = "id", type = IdType.AUTO)
    private int id;


    /**
     * 文章的id
     */
    @ApiModelProperty(value = "文章id", dataType = "int")
    @TableField(value = "aid", strategy = FieldStrategy.NOT_NULL)
    private int aid;


    /**
     * 分类的id
     */
    @ApiModelProperty(value = "分类id", dataType = "int")
    @TableField(value = "cid", strategy = FieldStrategy.NOT_NULL)
    private int cid;

    /**
     * 分类父级id，不做映射
     */
    @ApiModelProperty(value = "分类父级id, 不做映射", required = false, dataType = "int")
    @TableField(exist = false)
    private int parent_id;

    /**
     * 标签的id，可以设置5个， 例如 1,2,3,4,5
     */
    @ApiModelProperty(value = "所属的标签信息", dataType = "List")
    @TableField(exist = false)
    private List<ArticleLabel> lids;


    /**
     * 用户的id
     */
    @ApiModelProperty(value = "用户的id", dataType = "int")
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    private int uid;
}
