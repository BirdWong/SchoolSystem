package cn.jsuacm.ccw.controller.blog;

import cn.jsuacm.ccw.pojo.blog.Label;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.blog.LabelService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LabelController
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/22 21:26
 */
@RestController
@RequestMapping(value = "label")
@Api(value = "label", description = "标签操作")
public class LabelController {

    @Autowired
    private LabelService labelService;


    /**
     * 用户添加一个标签
     * @param label
     * @param req
     * @return
     */
    @PostMapping("add")
    @ApiOperation(value = "添加一个用户的标签", notes = "添加一个用户的标签， 需要进行token二次校验", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lname", required = true, value = "标签的名称", dataType =  "string", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query")
    })
    public MessageResult addLabel(@RequestBody Label label, HttpServletRequest req){
        if (CheckUserUtil.isUser(req, label.getUid())){
            MessageResult messageResult = labelService.addLabel(label);
            return messageResult;
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }


    /**
     * 用户修改一个标签
     * @param label
     * @param req
     * @return
     */
    @PostMapping(value = "update")
    @ApiOperation(value = "修改一个用户的标签", notes = "修改一个用户的标签， 需要进行token二次校验", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lid", required = true, value = "标签的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "lname", required = true, value = "标签的名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "标签所属的用户id", dataType = "int ", paramType = "query")
    })
    public MessageResult updateLabel(@RequestBody Label label, HttpServletRequest req){

        if (CheckUserUtil.isUser(req, label.getUid())){
            MessageResult messageResult = labelService.updateLable(label);
            return messageResult;
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }


    /**
     * 删除一个用户的标签
     * @param uid
     * @param lid
     * @param req
     * @return
     */
    @GetMapping(value = "delete/{uid}/{lid}")
    @ApiOperation(value = "删除一个用户的标签", notes = "通过用户的标签id删除一个标签， 需要进行token二次校验",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "lid", required = true, value = "标签的id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteLabel(@PathVariable(value = "uid") int uid, @PathVariable(value = "lid") int lid, HttpServletRequest req){
        if (CheckUserUtil.isUser(req, uid)){
            MessageResult messageResult = labelService.delteLable(lid);
            return messageResult;
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }


    /**
     * 通过用户的id获取这个用户的所有标签
     * @param uid
     * @param req
     * @return
     */
    @GetMapping(value = "getByUid/{uid}")
    @ApiOperation(value = "通过用户的id获取这个用户的所有标签信息", notes = "通过用户的id获取这个用户的所有标签信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<Label> getByUid(@PathVariable(value = "uid") int uid, HttpServletRequest req){

        if (CheckUserUtil.isUser(req, uid)){
            List<Label> labels = labelService.getByUid(uid);
            return labels;
        }else {
            return null;
        }
    }


    /**
     * 根据标签的id， 获取一个标签的信息
     * @param lid
     * @return
     */
    @GetMapping(value = "getById/{lid}")
    @ApiOperation(value = "根据标签的id，获取这个标签的信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lid", required = true, value = "标签的名称", dataType = "int", paramType = "path")
    })
    public Label getById(@PathVariable(value = "lid") int lid){
        Label label = labelService.getById(lid);
        return label;
    }


    /**
     * 获取所有的标签信息以及他们的用户
     * @return
     */
    @GetMapping(value = "getAll")
    @ApiOperation(value = "获取所有用户的所有标签", notes = "获取所有用户的所有标签信息， 以用户id为键， 标签列表为值")
    public Map<Integer , List<Label>> getAllLabel(){
        Map<Integer, List<Label>> labelList = labelService.getLabelList();
        return labelList;
    }

}
