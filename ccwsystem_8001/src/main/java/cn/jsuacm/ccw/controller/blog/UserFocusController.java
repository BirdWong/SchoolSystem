package cn.jsuacm.ccw.controller.blog;

import cn.jsuacm.ccw.pojo.blog.UserFocus;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.blog.UserFocusService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserFocusController
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/28 21:36
 */
@RestController
@RequestMapping(value = "focus")
@Api(value = "focus", description = "用户关注操作接口")
public class UserFocusController {

    @Autowired
    private UserFocusService userFocusService;

    /**
     * 添加一个关注
     * @param req
     * @param userFocus
     * @return
     */
    @PostMapping("add")
    @ApiOperation(value = "添加一个关注", notes = "添加一个关注， 必须关注者id和被关注者id都是存在的， 会进行二次token校验", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toUid", required = true, value = "被关注者id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "fromUid", required = true, value = "关注者id", dataType = "int", paramType = "query"),

    })
    public MessageResult addFocus(HttpServletRequest req, @RequestBody UserFocus userFocus){
        if (CheckUserUtil.isUser(req, userFocus.getFromUid())){
            return userFocusService.addFocus(userFocus);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


    /**
     * 通过记录id删除一条记录
     * @param req
     * @param id 记录id
     * @param uid 用户id
     * @return
     */
    @GetMapping("deleteById/{uid}/{id}")
    @ApiOperation(value = "通过记录id删除一条记录", notes = "通过记录id删除一条记录， uid是这条记录拥有者的id， 会进行二次token验证",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "关注者的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "id", required = true, value = "记录id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteById(HttpServletRequest req, @PathVariable(value = "id") int id, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return userFocusService.deleteFocusById(id, uid);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


    /**
     * 删除一个用户关注的所有记录
     * @param req
     * @param uid 用户的id
     * @return
     */
    @GetMapping("deleteByFromUid/{uid}")
    @ApiOperation(value = "删除一个用户的所有关注记录", notes = "删除一个用户的所有关注记录, 会进行二次token验证",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteByFromUid(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return userFocusService.deleteFocusByFromUid(uid);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }





    /**
     * 删除一个用户被关注的所有记录
     * @param req
     * @param uid 用户的id
     * @return
     */
    @GetMapping("deleteByToUid/{uid}")
    @ApiOperation(value = "删除一个用户的所有被关注记录", notes = "删除一个用户的所有被关注记录, 会进行二次token验证",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteByToUid(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return userFocusService.deleteFocusByToUid(uid);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


    /**
     * 获取一个用户的关注列表
     * @param req
     * @param uid 用户id
     * @return
     */
    @GetMapping("getFocusList/{uid}")
    @ApiOperation(value = "获取用户的关注列表， 会二次验证", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public List<UserFocus> getFocusList(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return userFocusService.getFocusList(uid);
        }else {
            return new ArrayList<>();
        }
    }







    /**
     * 获取一个用户的粉丝列表
     * @param req
     * @param uid 用户id
     * @return
     */
    @GetMapping("getFansList/{uid}")
    @ApiOperation(value = "获取用户的粉丝列表， 会二次验证", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public List<UserFocus> getFansList(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return userFocusService.getFansList(uid);
        }else {
            return new ArrayList<>();
        }
    }


    /**
     * 获取用户的关注数量
     * @param uid 用户id
     * @return
     */
    @GetMapping("getFocusCount/{uid}")
    @ApiOperation(value = "获取用户的关注数量", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult getFocusCount(@PathVariable(value = "uid")int uid){
        return userFocusService.getFocusCount(uid);
    }





    /**
     * 获取用户的粉丝数量
     * @param uid 用户id
     * @return
     */
    @GetMapping("getFansCount/{uid}")
    @ApiOperation(value = "获取用户的粉丝数量", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path")
    })
    public MessageResult getFansCount(@PathVariable(value = "uid")int uid){
        return userFocusService.getFansCount(uid);
    }


}
