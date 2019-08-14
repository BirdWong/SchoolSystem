package cn.jsuacm.ccw.controller.recruit;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.recruit.FormItemOption;
import cn.jsuacm.ccw.service.recruit.FormService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName FormController
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/07 17:15
 */
@RestController
@RequestMapping("form")
public class FormController {


    @Autowired
    private FormService formService;


    /**
     * 添加一个表单
     * @param formItemOptions
     * @return
     */
    @PostMapping("admin/add")
    @ApiOperation(value = "添加一个表单格式" , httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "表单的类型", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "label", value = "表单的标签名称", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "表单控件的键值", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "placeholder", value = "表单的提示信息", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "groupSpan", value = "表单的总长度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "labelSpan", value = "表单的标签长度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "formControlSpan", value = "表单的控件长度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "direction", value = "方向", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "options", value = "表单的选项", required = true, dataType = "array", paramType = "query"),
    })
    public MessageResult addForm(@RequestBody List<FormItemOption> formItemOptions){
        return formService.addForm(formItemOptions);
    }


    /**
     * 获取表单信息
     * @return
     */
    @GetMapping("get")
    @ApiOperation(value = "获取表单信息")
    public List<FormItemOption> getForm(){
        return formService.getForm();
    }


    /**
     * 更新表单信息
     * @param formItemOptions
     * @return
     */
    @PostMapping("admin/update")
    @ApiOperation(value = "更新表单信息", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "表单的类型", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "label", value = "表单的标签名称", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "表单控件的键值", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "placeholder", value = "表单的提示信息", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "groupSpan", value = "表单的总长度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "labelSpan", value = "表单的标签长度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "formControlSpan", value = "表单的控件长度", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "direction", value = "方向", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "options", value = "表单的选项", required = true, dataType = "array", paramType = "query"),
    })
    public MessageResult update(@RequestBody List<FormItemOption> formItemOptions){
        return formService.updateForm(formItemOptions);
    }


    /**
     * 删除表单信息
     * @return
     */
    @GetMapping(value = "admin/delete")
    @ApiOperation(value = "删除表单信息", httpMethod = "GET")
    public MessageResult delete(){
        return formService.deleteForm();
    }


}
