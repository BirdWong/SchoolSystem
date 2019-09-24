package cn.jsuacm.ccw.controller.annoucement;

import cn.jsuacm.ccw.pojo.announcement.AnnouncementCategory;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.announcement.AnnouncementCategoryService;
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
import java.util.Map;

/**
 * @ClassName AnnoucementCategory
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/22 19:01
 */
@RestController
@RequestMapping(value = "announcementCategory")
@Api(value = "公告分类设置")
public class AnnouncementCategoryController {

    @Autowired
    private AnnouncementCategoryService service;


    /**
     * 添加一个公告的分类， 设置公告的权限不能高于添加者的权限,并且只有管理员以上的权限才可以进行添加操作
     * @param map
     * @param req
     * @return
     */
    @PostMapping(value = "admin/add")
    @ApiOperation(value = "添加一个公告的分类", notes = "添加一个公告的分类， 设置公告的权限不能高于添加者的权限,并且只有管理员以上的权限才可以进行添加操作, 分类的数量不能超过十个",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "这个添加者的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "role", required = true, value = "一共有四个权限，ALL代表任何人都可以看，ROLE_ING代表只有在实验室的成员才可以看，ROLE_ADMIN代表只有管理员才能看，ROLE_ADMINISTRATOR代表只有超级管理员才能够查看", dataType = "enum", paramType = "query"),
            @ApiImplicitParam(name = "name", required = true, value = "这个分类的名称， 如果已经存在就无法添加", dataType = "string", paramType = "query")
    })
    public MessageResult add(@RequestBody Map<String, Object> map, HttpServletRequest req){
        // 捕捉中间运行的错误
        try {
            // 获取uid， 可能报null、 数字转换错误
            Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
            // 校验是否是本人
            if (CheckUserUtil.isUser(req, uid)){
                // 提取分类的信息
                AnnouncementCategory announcementCategory = new AnnouncementCategory();
                announcementCategory.setName(String.valueOf(map.get("name")));
                announcementCategory.setRole(AnnouncementCategory.Role.valueOf(String.valueOf(map.get("role"))));
                // 添加信息
                return service.add(uid, announcementCategory);
            }else {
                return new MessageResult(false, "非本人操作");
            }
        }catch (Exception e){
            return new MessageResult(false, "参数传输错误");
        }

    }


    /**
     * 通过一个id获取一个分类的信息
     * @param id
     * @return
     */
    @GetMapping("getById/{id}")
    @ApiOperation(value = "通过一个id获取一个分类信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "分类的id", dataType = "int", paramType = "path")
    })
    public AnnouncementCategory getById(@PathVariable(value = "id") int id){
        return service.getForId(id);
    }


    /**
     * 修改一个公告的分类， 设置公告的权限不能高于修改者的权限,并且只有管理员以上的权限才可以进行修改操作
     * @param map
     * @param req
     * @return
     */
    @PostMapping(value = "admin/update")
    @ApiOperation(value = "修改一个公告的分类", notes = "修改一个公告的分类， 设置公告的权限不能高于修改者的权限,并且只有管理员以上的权限才可以进行修改操作",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "这个添加者的id， 如果id不存在或者id所属权限大于目前操作者权限则提示失败", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "role", required = true, value = "一共有四个权限，ALL代表任何人都可以看，ROLE_ING代表只有在实验室的成员才可以看，ROLE_ADMIN代表只有管理员才能看，ROLE_ADMINISTRATOR代表只有超级管理员才能够查看", dataType = "enum", paramType = "query"),
            @ApiImplicitParam(name = "name", required = true, value = "这个分类的名称， 如果已经存在就无法添加", dataType = "string", paramType = "query")
    })
    public MessageResult update(@RequestBody Map<String, Object> map, HttpServletRequest req){
        // 捕捉中间运行的错误
        try {
            // 获取uid， 可能报null、 数字转换错误
            Integer uid = Integer.valueOf(String.valueOf(map.get("uid")));
            // 校验是否是本人
            if (CheckUserUtil.isUser(req, uid)){
                // 提取分类的信息
                AnnouncementCategory announcementCategory = new AnnouncementCategory();
                announcementCategory.setName(String.valueOf(map.get("name")));
                announcementCategory.setRole(AnnouncementCategory.Role.valueOf(String.valueOf(map.get("role"))));
                announcementCategory.setId(Integer.valueOf(String.valueOf(map.get("id"))));
                // 添加信息
                return service.update(uid, announcementCategory);
            }else {
                return new MessageResult(false, "非本人操作");
            }
        }catch (Exception e){
            return new MessageResult(false, "参数传输错误");
        }
    }


    /**
     * 通过公告的id删除一个公告， 如果操作者的权限没有这个公告设置的权限高则无法操作，如果公告分类下有公告也无法删除
     * @param uid 用户id
     * @param id 分类的id
     * @return
     */
    @PutMapping("admin/delete")
    @ApiOperation(value = "删除一个公告", notes = "通过公告的id删除一个公告， 如果操作者的权限没有这个公告设置的权限高则无法操作，如果公告分类下有公告也无法删除", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "这个分类的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "操作者的id", dataType = "int", paramType = "query")
    })
    public MessageResult delete(@RequestParam(value = "uid") int uid, @RequestParam(value = "id") int id, HttpServletRequest req){
        // 校验是否本人操作
        if (CheckUserUtil.isUser(req, uid)){
            return service.delete(uid, id);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


    /**
     * 对应的权限是ALL, 表示任何人都能看到的分类，不需要任何用户权限的
     * @return
     */
    @GetMapping(value = "getOfAll")
    @ApiOperation(value = "获取所有人都能看到的分类", notes = "对应的权限是ALL, 表示任何人都能看到的分类，不需要任何用户权限的", httpMethod = "GET")
    public List<AnnouncementCategory> getOfAll(){
        return service.getOfAll();
    }


    /**
     * 获取正在实验室用户能够看到的， 说明ING权限和ALL权限的分类都能够看到
     * @param uid 用户的id
     * @param req
     * @return
     */
    @GetMapping(value = "getOfIng/{uid}")
    @ApiOperation(value = "正在实验室用户权限能够看到的分类", notes = "获取正在实验室用户能够看到的， 说明ING权限和ALL权限的分类都能够看到", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id,用于校验权限，如果权限不够是不会返回内容的", dataType = "int", paramType = "path")
    })
    public List<AnnouncementCategory> getOfIng(@PathVariable(value = "uid") int uid, HttpServletRequest req){
        // 验证是本人操作
        if (CheckUserUtil.isUser(req, uid)){
            return service.getOfIng(uid);
        }else {
            return new ArrayList<>();
        }

    }




    /**
     * 获取管理员用户能够看到的， 说明ADMIN权限和ING权限和ALL权限的分类都能够看到
     * @param uid 用户的id
     * @param req
     * @return
     */
    @GetMapping(value = "getOfAdmin/{uid}")
    @ApiOperation(value = "管理员用户权限能够看到的分类", notes = "说明ADMIN权限和ING权限和ALL权限的分类都能够看到", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id,用于校验权限，如果权限不够是不会返回内容的", dataType = "int", paramType = "path")
    })
    public List<AnnouncementCategory> getOfAdmin(@PathVariable(value = "uid") int uid, HttpServletRequest req){
        // 验证是本人操作
        if (CheckUserUtil.isUser(req, uid)){
            return service.getOfAdmin(uid);
        }else {
            return new ArrayList<>();
        }

    }




    /**
     * 获取超级管理员用户能够看到的， 说明Administrator权限和Admin权限和ING权限和ALL权限的分类都能够看到， 相当是全部的分类信息都会返回
     * @param uid 用户的id
     * @param req
     * @return
     */
    @GetMapping(value = "getOfAdministrator/{uid}")
    @ApiOperation(value = "超级管理员用户权限能够看到的分类", notes = "获取超级管理员用户能够看到的， 说明Administrator权限和Admin权限和ING权限和ALL权限的分类都能够看到，相当是全部的分类信息都会返回", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id,用于校验权限，如果权限不够是不会返回内容的", dataType = "int", paramType = "path")
    })
    public List<AnnouncementCategory> getOfAdministrator(@PathVariable(value = "uid") int uid, HttpServletRequest req){
        // 验证是本人操作
        if (CheckUserUtil.isUser(req, uid)){
            return service.getOfAdministrator(uid);
        }else {
            return new ArrayList<>();
        }
    }
}
