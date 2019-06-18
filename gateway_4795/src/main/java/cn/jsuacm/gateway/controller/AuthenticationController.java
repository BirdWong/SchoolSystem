package cn.jsuacm.gateway.controller;

import cn.jsuacm.gateway.pojo.enity.MessageResult;
import cn.jsuacm.gateway.service.AuthenticationService;
import cn.jsuacm.gateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName AuthenticationController
 * @Description 权限管理，增删权限，以及用户密码更改
 * @Author h4795
 * @Date 2019/06/18 16:46
 */
@RestController
@RequestMapping("authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "updatePassword")
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
    @RequestMapping(value = "add/menber", method = RequestMethod.GET)
    public MessageResult addMemberRole(int uid){
        boolean status = authenticationService.addMemBerAuthentication(uid);
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
    @RequestMapping(value = "add/admin", method = RequestMethod.GET)
    public MessageResult addAdminRole(int uid){
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
    @RequestMapping(value = "add/teacher", method = RequestMethod.GET)
    public MessageResult addTeacherRole(int uid){
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
    @RequestMapping(value = "add/administrator", method = RequestMethod.GET)
    public MessageResult addAdministratorRole(int uid){
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
    @RequestMapping(value = "/delete/menber", method = RequestMethod.GET)
    public MessageResult deleteMemberRole(int uid){
        boolean status = authenticationService.deleteMemBerAuthentication(uid);
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
    @RequestMapping(value = "/delete/admin", method = RequestMethod.GET)
    public MessageResult deleteAdminRole(int uid){
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
    @RequestMapping(value = "/delete/teacher", method = RequestMethod.GET)
    public MessageResult deleteTeacherRole(int uid){
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
    @RequestMapping(value = "/delete/administrator", method = RequestMethod.GET)
    public MessageResult deleteAdministratorRole(int uid){
        boolean status = authenticationService.deleteAdministratorAuthentication(uid);
        if (status){
            return new MessageResult(status, "success");
        }else {
            return new MessageResult(status, "没有这个id");
        }
    }




}
