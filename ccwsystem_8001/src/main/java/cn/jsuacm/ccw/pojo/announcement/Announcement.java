package cn.jsuacm.ccw.pojo.announcement;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName AnnouncementMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/23 17:11
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "announcement")
@ApiModel("公告文章，文章要求： 1. title标题不为空长度长度不大于50字 2. html文章内容不为空 3. markdown文本不为空  4. cid必须存在 5. 如果是修改更新文章必须有创建时间参数  6. 公告的任何操作只能操作自己权限能够操作的分类， （自己的权限>分类权限）")
public class Announcement {


    /**
     * 公告的id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "公告的id", dataType = "int")
    private int id;


    /**
     * 公告的标题
     */
    @TableField(value = "title", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "公告的标题", dataType = "string")
    private String title;


    /**
     * 公告发布时的时间
     */
    @TableField(value = "create_time", strategy = FieldStrategy.DEFAULT)
    @ApiModelProperty(value = "发布的时间", dataType = "date")
    private Date createTime;


    /**
     * 公告修改的时间
     */
    @TableField(value = "modify_time", strategy = FieldStrategy.DEFAULT)
    @ApiModelProperty(value = "修改的时间", dataType = "date")
    private Date modifyTime;


    /**
     * html文本内容
     */
    @TableField(value = "html_content", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "html格式的公告内容", dataType = "string")
    private String htmlContent;


    /**
     * markdown格式文本
     */
    @TableField(value = "content", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "文本markdown格式", dataType = "string")
    private String content;


    /**
     * 查看量
     */
    @TableField(value = "views")
    @ApiModelProperty(value = "公告查看的人数", dataType = "int")
    private int views;


    /**
     * 关联的分类id
     */
    @TableField(value = "cid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "所属的分类id", dataType = "int")
    private int cid;
}
