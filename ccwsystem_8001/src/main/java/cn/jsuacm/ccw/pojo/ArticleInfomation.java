package cn.jsuacm.ccw.pojo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ArticleInfomation implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "文章信息表中的id")
    @TableId(value = "id", type = IdType.AUTO)
    private int id;


    /**
     * 文章的id
     */
    @ApiModelProperty(value = "文章id")
    @TableField(value = "aid", strategy = FieldStrategy.NOT_NULL)
    private int aid;


    /**
     * 分类的id
     */
    @ApiModelProperty(value = "分类id")
    @TableField(value = "cid", strategy = FieldStrategy.NOT_NULL)
    private int cid;

    /**
     * 分类父级id，不做映射
     */
    @ApiModelProperty(value = "分类父级id")
    @TableField(exist = false)
    private int parent_id;

    /**
     * 标签的id，可以设置5个， 例如 1,2,3,4,5
     */
    @ApiModelProperty(value = "所属的标签信息")
    @TableField(exist = false)
    private List<ArticleLabel> lids;


    /**
     * 用户的id
     */
    @ApiModelProperty(value = "用户的id")
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    private int uid;
}
