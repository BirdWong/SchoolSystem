package cn.jsuacm.gateway.controller;

import cn.jsuacm.gateway.pojo.User;
import cn.jsuacm.gateway.pojo.enity.MessageResult;
import cn.jsuacm.gateway.pojo.enity.PageResult;
import cn.jsuacm.gateway.service.UserService;
import cn.jsuacm.gateway.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description 用户控制层
 * @Author h4795
 * @Date 2019/06/17 20:02
 */
@RestController
@RequestMapping(value = "user")
@Api(value = "user", description = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 发送注册邮件
     * @param map 信息包含email地址
     * @return
     */
    @PostMapping(value = "sendRegisterEmail")
    @ApiOperation(value = "发送注册邮件", notes = "发送用户注册邮件。 一分钟之内只能发送一次")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", required = true, value = "邮箱", dataType = "string", paramType = "query")
    })
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
    @ApiOperation(value = "注册新用户", notes = "访问这个接口前用户密码必须在前台验证是否一致")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", required = true, value = "用户昵称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "email", required = true, value = "邮箱", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", required = true, value = "密码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "accountNumber", required = true, value = "用户的账户 唯一", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "code", required = true, value = "验证码", dataType = "string", paramType = "query")
    })
    public MessageResult registerUser(@RequestBody  Map<String, Object> map){
        User user = new User();
        user.setUsername(String.valueOf(map.get("username")));
        user.setEmail(String.valueOf(map.get("email")));
        user.setPassword(String.valueOf(map.get("password")));
        user.setAccountNumbser(String.valueOf(map.get("accountNumbser")));
        String code = String.valueOf(map.get("code"));
        return userService.registerUser(user, code);
    }


    /**
     * 通过邮箱验证更新密码
     * @param map 信息包含 用户email， 新的密码， 验证码
     * @return
     */
    @PostMapping(value = "updatePasswordByEmail")
    @ApiOperation(value = "通过邮箱修改新密码", notes = "通过用户的邮箱修改新密码，可以用于改密。 也可以用于找回密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", required = true, value = "邮箱", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "newPwd", required = true, value = "新密码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "code", required = true, value = "验证码", dataType = "string", paramType = "query")
    })
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
    @ApiOperation(value = "通过旧密码修改密码", notes = "通过旧密码修改密码，需要token二次校验",httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "oldPwd", required = true, value = "旧密码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "newPwd", required = true, value = "新密码", dataType = "string", paramType = "query")
    })
    public MessageResult updatePassword(HttpServletRequest req,@RequestBody Map<String, Object> map){
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)) {
            String oldPwd = String.valueOf(map.get("oldPwd"));
            String newPwd = String.valueOf(map.get("newPwd"));
            return userService.updatePassword(uid, oldPwd, newPwd);
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }

    /**
     * 更新用户名
     * @param map 信息包含用户uid ， 用户名
     * @return
     */
    @PostMapping(value = "updateUsername")
    @ApiOperation(value = "更新用户名", notes = "更新用户名， 需要token二次校验", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "username", required = true, value = "用户名称", dataType = "string",paramType = "query")

    })
    public MessageResult updateUsername(@RequestBody Map<String, Object> map, HttpServletRequest req){
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            String username = String.valueOf("username");
            MessageResult messageResult = userService.updateUsername(uid, username);
            return messageResult;
        }else {
            return new MessageResult(false, "不是本人操作");
        }


    }


    /**
     * 更新用户的头像url
     * @param map 信息包含，用户的uid， 头像url
     * @return
     */
    @PostMapping(value = "updateUrl")
    @ApiOperation(value = "更新用户的头像链接", notes = "修改用户的头像链接，会进行token二次验证", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "url", required = true, value = "头像链接", dataType = "int", paramType = "query")
    })
    public MessageResult updatePicUrl(@RequestBody Map<String, Object> map, HttpServletRequest req){
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)){
            String url = String.valueOf(map.get("url"));
            MessageResult messageResult = userService.updatePicUrl(uid, url);
            return messageResult;
        }else {
            return new MessageResult(false, "不是本人操作");
        }

    }


    /**
     * 更新用户邮箱
     * @param map 信息包含用户的uid， 用户的邮箱， 验证码
     * @return
     */
    @PostMapping(value = "updateEmail")
    @ApiOperation(value = "更新用户邮箱信息", notes = "用户修改自己的绑定邮箱，首先需要发送修改信息邮件， 会进行token二次验证是否是本人操作", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "email", required = true, value = "用户的新邮箱", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "code", required = true, value = "验证码", dataType = "string", paramType = "query")
    })
    public MessageResult updateEmail(HttpServletRequest req,@RequestBody Map<String, Object> map) {
        Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
        if (CheckUserUtil.isUser(req, uid)) {
            String email = String.valueOf(map.get("email"));
            String code = String.valueOf(map.get("code"));
            MessageResult messageResult = userService.updateEmail(uid, email, code);
            return messageResult;
        }else {
            return new MessageResult(false, "不是本人操作");
        }
    }


    /**
     * 发送更改信息的邮件
     * @param map
     * @return
     */
    @PostMapping(value = "sendUpdateEmail")
    @ApiOperation(value = "发送修改信息的验证码", notes = "发送信息修改验证码可以用于修改邮箱， 密码。 找回密码等功能", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", required = true, value = "邮箱账户", dataType = "string", paramType = "query")
    })
    public MessageResult sendUpdateEmail(@RequestBody Map<String,Object> map){
        String email = String.valueOf(map.get("email"));
        MessageResult messageResult = userService.sendUpdateEmail(email);
        return messageResult;
    }


    /**
     * 返回所有用户信息以及其权限
     * @param row
     * @param pageSize
     * @return
     */
    @GetMapping(value = "getPage/{row}/{size}")
    @ApiOperation(value = "返回所有用户信息以及用户的权限", notes = "用户信息固定， 但是权限数量未知， 是list保存", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "row", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "size", required = true, value = "页面大小", dataType = "int", paramType = "path")
    })
    public PageResult<UserService.UserAuthentication> getAll(@PathVariable(value = "row") int row, @PathVariable(value = "size") int pageSize){
        return userService.getUserAuthentication(row, pageSize);
    }


    /**
     * 判断是否有这个用户
     * @param uid 用户id
     * @return
     */
    @GetMapping(value = "isUser/{uid}")
    @ApiOperation(value = "判断是否存在这个用户", notes = "判断用户是否存在，用于其他模块检测用户，防止数据库插入异常")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public boolean isUser(@PathVariable(value = "uid") int uid){
        return userService.isUser(uid);
    }


    /**
     * 获取一个用户的信息
     * @param uid
     * @return
     */
    @GetMapping("getUser/{uid}")
    @ApiOperation(value = "获取一个用户的信息", notes = "获取一个用户的信息， 会自动去除敏感信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public User getUser(@PathVariable(value = "uid") int uid){
        User user = userService.getById(uid);
        //去除敏感信息
        String email = user.getEmail();
        int index = email.indexOf("@");
        if (index > 6){
            email = email.substring(0,3)+"***"+email.substring(index-3, email.length());
        }else if(index > 2){
            email = email.substring(0,1)+"***"+email.substring(index - 1, email.length());
        }
        user.setEmail(email);
        user.setPassword("");
        return user;
    }
}
