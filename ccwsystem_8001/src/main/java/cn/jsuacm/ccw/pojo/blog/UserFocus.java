package cn.jsuacm.ccw.pojo.blog;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserFocusMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/28 20:28
 */
@TableName(value = "user_focus")
@Data
@ApiModel(value = "用户关注或者黑名单列表", description = "用于用户的关系，status属性可以作用区分是黑名单还是关注， 目前阶段可能无用")
public class UserFocus implements Serializable{

    /**
     * 用户关注列表的id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id", dataType = "int")
    private int id;

    /**
     * 关注的人的id
     */
    @TableField(value = "from_uid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "关注者的id", required = true, dataType = "int")
    private int fromUid;

    /**
     * 被关注的人的id
     */
    @TableField(value = "to_uid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "被关注者的uid", required = true, dataType = "int")
    private int toUid;


    /**
     * 用于标记是关注还是黑名单
     */
    @ApiModelProperty(value = "暂时没得用")
    private int status;
}
