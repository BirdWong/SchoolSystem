package cn.jsuacm.ccw.pojo.recruit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName FormItemOption
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/06 17:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "前端组件实体类")
public class FormItemOption implements Serializable{

    @ApiModelProperty(value = "表单的类型", required = true, dataType = "string")
    private String type;


    @ApiModelProperty(value = "表单的标签名称", required = true, dataType = "string")
    private String label;


    @ApiModelProperty(value = "表单控件的键值", required = true, dataType = "string")
    private String name;


    @ApiModelProperty(value = "表单的提示信息", required = true, dataType = "string")
    private String placeholder;


    @ApiModelProperty(value = "表单的总长度", required = true, dataType = "int")
    private Integer groupSpan;


    @ApiModelProperty(value = "标签长度", required = true, dataType = "int")
    private Integer labelSpan;

    @ApiModelProperty(value = "表单的控件长度", required = true, dataType = "int")
    private Integer formControlSpan;

    @ApiModelProperty(value = "方向", required = true, dataType = "string")
    private String direction;

    @ApiModelProperty(value = "表单的选项", required = true, dataType = "array")
    private String[] options;
}
