package cn.jsuacm.ccw.pojo.projects;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Collaborators
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 10:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "collaborators")
@ApiModel(value = "项目人员名单")
public class Collaborators {


    /**
     * 人员名单id
     */
    @ApiModelProperty(value = "人员名单id", dataType = "int")
    @TableId(value = "id", type = IdType.AUTO)
    private int id;


    /**
     * 项目id
     */
    @ApiModelProperty(value = "项目id", dataType = "int")
    @TableField(value = "pid", strategy = FieldStrategy.NOT_NULL)
    private int pid;


    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户的id", dataType = "int")
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    private int uid;


    /**
     * 项目的url地址
     */
    @ApiModelProperty(value = "项目的url地址", dataType = "string")
    @TableField(value = "url")
    private String url;


    /**
     * github 用户名称
     */
    @ApiModelProperty(value = "github用户名称", dataType = "string")
    @TableField(value = "name")
    private String name;
}
