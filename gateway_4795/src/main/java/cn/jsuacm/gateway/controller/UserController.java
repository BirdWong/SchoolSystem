package cn.jsuacm.gateway.controller;

import cn.jsuacm.gateway.pojo.User;
import cn.jsuacm.gateway.pojo.enity.MessageResult;
import cn.jsuacm.gateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName UserController
 * @Description 用户控制层
 * @Author h4795
 * @Date 2019/06/17 20:02
 */
@RestController
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 发送注册邮件
     * @param map 信息包含email地址
     * @return
     */
    @PostMapping(value = "sendRegisterEmail")
    public MessageResult sendRegisterEmail(@RequestBody Map<String, Object> map){
        String email = String.valueOf(map.get("email"));
        MessageResult messageResult = userService.sendRegisterEmail(email);
        return messageResult;
    }


    /**
     * 注册用户
     * @param map 信息包含用户名，账号，密码，邮箱，验证码
     * @return
     */
    @PostMapping(value = "registerUser")
    public MessageResult registerUser(@RequestBody  Map<String, Object> map){
        User user = new User();
        user.setUsername(String.valueOf(map.get("username")));
        user.setEmail(String.valueOf(map.get("email")));
        user.setPassword(String.valueOf(map.get("password")));
        user.setAccountNumbser(String.valueOf(map.get("accountNumbser")));
        String code = String.valueOf(map.get("code"));
        MessageResult messageResult = userService.registerUser(user, code);
        return messageResult;
    }


    /**
     * 通过邮箱验证更新密码
     * @param map 信息包含 用户email， 新的密码， 验证码
     * @return
     */
    @PostMapping(value = "updatePasswordByEmail")
    public MessageResult updatePasswordByEmail(@RequestBody Map<String, Object> map){
        String email = String.valueOf(map.get("email"));
        String newPwd = String.valueOf(map.get("newPwd"));
        String code = String.valueOf(map.get("code"));
        MessageResult messageResult = userService.updatePasswordByEmail(email, newPwd, code);
        return messageResult;
    }


    /**
     * 通过旧密码修改密码
     * @param map 信息包含用户uid， 旧密码， 新密码
     * @return
     */
    @PostMapping(value = "updatePassword")
    public MessageResult updatePassword(@RequestBody Map<String, Object> map){
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        String oldPwd = String.valueOf(map.get("oldPwd"));
        String newPwd = String.valueOf(map.get("newPwd"));
        MessageResult messageResult = userService.updatePassword(uid, oldPwd, newPwd);
        return  messageResult;

    }

    /**
     * 更新用户名
     * @param map 信息包含用户uid ， 用户名
     * @return
     */
    @PostMapping(value = "updateUsername")
    public MessageResult updateUsername(@RequestBody Map<String, Object> map){
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        String username = String.valueOf("username");
        MessageResult messageResult = userService.updateUsername(uid, username);
        return messageResult;
    }


    /**
     * 更新用户的头像url
     * @param map 信息包含，用户的uid， 头像url
     * @return
     */
    @PostMapping(value = "updateUrl")
    public MessageResult updatePicUrl(@RequestBody Map<String, Object> map){
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        String url = String.valueOf(map.get("url"));
        MessageResult messageResult = userService.updatePicUrl(uid, url);
        return messageResult;
    }


    /**
     * 更新用户邮箱
     * @param map 信息包含用户的uid， 用户的邮箱， 验证码
     * @return
     */
    @PostMapping(value = "updateEmail")
    public MessageResult updateEmail(@RequestBody Map<String, Object> map){
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        String email = String.valueOf(map.get("email"));
        String code = String.valueOf(map.get("code"));
        MessageResult messageResult = userService.updateEmail(uid, email, code);
        return messageResult;
    }


    /**
     * 发送更改信息的邮件
     * @param map
     * @return
     */
    @PostMapping(value = "sendUpdateEmail")
    public MessageResult sendUpdateEmail(@RequestBody Map<String,Object> map){
        String email = String.valueOf(map.get("email"));
        MessageResult messageResult = userService.sendUpdateEmail(email);
        return messageResult;
    }



}
