package cn.jsuacm.ccw.pojo.projects;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName Commit
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 09:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "项目提交信息实体")
@TableName(value = "commit")
public class Commit implements Serializable{

    /**
     * 提交id
     */
    @ApiModelProperty(value = "提交的id", dataType = "int")
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    /**
     * 每次提交的sha
     */
    @TableField(value = "sha", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "每一次提交生成的sha码， 用来标识一次提交", required = true, dataType = "string")
    private String sha;


    /**
     * 提交更新描述
     */
    @TableField(value = "message", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "提交更新描述", required = true, dataType = "string")
    private String message;


    /**
     * 提交内容页面
     */
    @TableField(value = "url", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "提交更新html页面",required = true, dataType = "string")
    private String htmlUrl;


    /**
     * 项目人员id
     */
    @TableField(value = "cid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "项目合作者id", required = true, dataType = "int")
    private int cid;

    /**
     * 提交的用户名
     */
    @ApiModelProperty(value = "名称", required = true, dataType = "string")
    @TableField(value = "name", strategy = FieldStrategy.NOT_NULL)
    private String name;

    /**
     * 邮箱地址
     */
    @TableField(value = "email", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "邮箱", required = true, dataType = "string")
    private String email;

    /**
     * 提交时间
     */
    @TableField(value = "date", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "提交时间", required = true, dataType = "datetime")
    private Date date;


}

