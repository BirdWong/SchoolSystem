package cn.jsuacm.ccw.pojo.projects;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName Project
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 09:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "项目信息")
@TableName(value = "project")
public class Project {

    /**
     * 项目开启状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目开启状态", required = false)
    public static final int PROJECT_OPEN = 1;



    /**
     * 项目关闭状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "项目关闭状态", required = false)
    public static final int PROJECT_CLOSE = -1;






    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "项目id", dataType = "int")
    private int id;

    /**
     * 项目的名称
     */
    @TableField(value = "name", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "项目名称", dataType = "string")
    private String name;


    /**
     * 项目的描述
     */
    @TableField(value = "description", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "项目描述", dataType = "string")
    private String description;


    /**
     * 项目描述html
     */
    @TableField(value = "html_description", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "项目描述html格式", dataType = "string")
    private String htmlDescription;

    /**
     * 项目状态
     */
    @ApiModelProperty(value = "项目状态", dataType = "string")
    @TableField(value = "status", strategy = FieldStrategy.NOT_NULL)
    private int status;


    /**
     * 项目的开始时间
     */
    @ApiModelProperty(value = "项目开始时间", dataType = "date")
    @TableField(value = "date")
    private Date date;


    /**
     * 项目总共花费时间
     */
    @ApiModelProperty(value = "项目总时间", dataType = "int")
    @TableField(value = "days")
    private int days;
}
