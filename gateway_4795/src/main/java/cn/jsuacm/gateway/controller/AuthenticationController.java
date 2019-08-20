package cn.jsuacm.gateway.controller;

import cn.jsuacm.gateway.pojo.Authentication;
import cn.jsuacm.gateway.pojo.enity.MessageResult;
import cn.jsuacm.gateway.pojo.enity.PageResult;
import cn.jsuacm.gateway.service.AuthenticationService;
import cn.jsuacm.gateway.service.UserService;
import cn.jsuacm.gateway.util.CheckUserUtil;
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
 * @ClassName AuthenticationController
 * @Description 权限管理，增删权限，以及用户密码更改
 * @Author h4795
 * @Date 2019/06/18 16:46
 */
@RestController
@RequestMapping("authentication")
@Api(value = "authentication", description = "权限操作接口")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;


    /**
     * 修改用户密码接口
     * @param map
     * @return
     */
    @PostMapping(value = "updatePassword")
    @ApiOperation(value = "修改用户的密码", notes = "给指定id修改新密码", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "newPwd", required = true, value = "新的密码", dataType = "string", paramType = "query")
    })
    public MessageResult updatePassword(@RequestBody Map<String, Object> map){
        int uid = Integer.valueOf(String.valueOf(map.get("uid")));
        String newPwd = String.valueOf(map.get("newPwd"));
        MessageResult messageResult = userService.updatePassword(uid, newPwd);
        return messageResult;
    }


    /**
     * 添加工作室成员
     * @param uid 用户的id
     * @return
     */
    @RequestMapping(value = "add/menber/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "添加新的工作室成员", notes = "添加新的工作室成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult addMemberRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.addMemBerAuthentication(uid);
        if (status){
            return new MessageResult(status,"success");
        }else{
            return new MessageResult(status,"没有这个uid");
        }
    }


    /**
     * 添加正在工作室成员
     * @param uid 用户的id
     * @return
     */
    @RequestMapping(value = "add/ing/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "添加识别信息， 确认是否已经毕业， 如果没有这个权限只有menber权限会认为是毕业学长， 不会进入管理部分, 删除用户实验室成员权限时会自动删除此权限", notes = "添加新的工作室成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult addIngRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.addIngAuthentication(uid);
        if (status){
            return new MessageResult(status,"success");
        }else{
            return new MessageResult(status,"没有这个uid");
        }
    }




    /**
     * 添加管理员权限
     * @param uid 用户的id
     * @return
     */
    @RequestMapping(value = "add/admin/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "添加新的管理员", notes = "添加新的管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult addAdminRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.addAdminAuthentication(uid);
        if (status){
            return new MessageResult(status,"success");
        }else{
            return new MessageResult(status,"没有这个uid");
        }
    }


    /**
     * 添加老师权限
     * @param uid 用户的id
     * @return
     */
    @RequestMapping(value = "add/teacher/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "对应平台未开发，可以不用管")
    public MessageResult addTeacherRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.addTeacherAuthentication(uid);
        if (status){
            return new MessageResult(status,"success");
        }else{
            return new MessageResult(status,"没有这个uid");
        }
    }


    /**
     * 添加超级管理员权限
     * @param uid 用户的id
     * @return
     */
    @RequestMapping(value = "add/administrator/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "添加超级管理员权限", notes = "添加超级管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult addAdministratorRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.addAdministratorAuthentication(uid);
        if (status){
            return new MessageResult(status,"success");
        }else{
            return new MessageResult(status,"没有这个uid");
        }
    }


    /**
     * 删除工作室成员权限
     * @param uid 用户id
     * @return
     */
    @RequestMapping(value = "delete/menber/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "删除工作室成员", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteMemberRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.deleteMemBerAuthentication(uid);
        if (status){
            return new MessageResult(status, "success");
        }else {
            return new MessageResult(status, "没有这个id");
        }
    }




    /**
     * 删除正在工作室成员权限
     * @param uid 用户id
     * @return
     */
    @RequestMapping(value = "delete/ing/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "删除正在工作室成员权限， 删除后这个人就是毕业了的学长， 不在管理范围内", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteIngRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.deleteIngAuthentication(uid);
        if (status){
            return new MessageResult(status, "success");
        }else {
            return new MessageResult(status, "没有这个id");
        }
    }



    /**
     * 删除管理员权限
     * @param uid 用户id
     * @return
     */
    @RequestMapping(value = "delete/admin/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "删除管理员", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteAdminRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.deleteAdminAuthentication(uid);
        if (status){
            return new MessageResult(status, "success");
        }else {
            return new MessageResult(status, "没有这个id");
        }
    }


    /**
     * 删除工作室成员权限
     * @param uid 用户id
     * @return
     */
    @RequestMapping(value = "delete/teacher/{uid}", method = RequestMethod.GET)
    @ApiOperation("对应平台未开发")
    public MessageResult deleteTeacherRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.deleteTeacherAuthentication(uid);
        if (status){
            return new MessageResult(status, "success");
        }else {
            return new MessageResult(status, "没有这个id");
        }
    }


    /**
     * 删除超级管理员的权限
     * @param uid 用户id
     * @return
     */
    @RequestMapping(value = "delete/administrator/{uid}", method = RequestMethod.GET)
    @ApiOperation(value = "删除超级管理员", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteAdministratorRole(@PathVariable(value="uid") int uid){
        boolean status = authenticationService.deleteAdministratorAuthentication(uid);
        if (status){
            return new MessageResult(status, "success");
        }else {
            return new MessageResult(status, "没有这个id");
        }
    }

    /**
     * 按照分页返回用户的权限
     * @param row
     * @param pageSize
     * @return
     */
    @GetMapping(value = "getPage/{row}/{size}")
    @ApiOperation(value = "按照分页返回权限列表",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "size", required = true, value = "页面大小",dataType = "int", paramType = "path")
    })
    public PageResult<AuthenticationService.UserAuthentication> getUserAuthentications(@PathVariable("row") int row, @PathVariable("size") int pageSize){
        return authenticationService.getUserAuthentication(row, pageSize);
    }




    @GetMapping(value = "getAuthenticationByUid/{uid}")
    @ApiOperation(value = "通过用户的id获取这个用户的所有权限", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "string", paramType = "path")
    })
    public List<Authentication> getAuthenticationByUid(HttpServletRequest req, @PathVariable(value = "uid")int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return authenticationService.getAuthenticationByUid(uid);
        }
        return null;
    }


}
