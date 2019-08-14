package cn.jsuacm.ccw.controller.project;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.projects.Collaborators;
import cn.jsuacm.ccw.service.projects.CollaboratorsService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @ClassName CollaboratorsController
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/02 15:59
 */
@RestController
@RequestMapping(value = "collaborators")
@Api(value = "项目成员操作")
public class CollaboratorsController {

    @Autowired
    private CollaboratorsService collaboratorsService;


    /**
     * 为一个项目添加成员
     * @param set 包含成员的set
     * @return
     */
    @PostMapping(value = "admin/add")
    @ApiOperation(value = "添加一个项目需要参加的人员", notes = "实验室成员列表可以通过user模块的接口获取，添加一个项目的成员， 例如： 大二的某某、、等12个人都需要做这个项目,发送一个集合，但是不能有重复， 否则会添加失败", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", required = true, value = "项目的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query")
    })
    public MessageResult addCollaborators(@RequestBody Set<Collaborators>set){
        return collaboratorsService.addCollaborators(set);
    }


    /**
     * 通过一个id删除信息
     * @param id
     * @return
     */
    @ApiOperation(value = "通过成员信息表的id将成员从其项目中删除", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "成员信息表的id", dataType = "int", paramType = "path")
    })
    @GetMapping(value = "admin/delete/{id}")
    public MessageResult deleteById(@PathVariable(value = "id") int id){
        return collaboratorsService.deleteById(id);
    }


    /**
     * 通过项目的id获取这个项目的成员信息列表
     * @param pid
     * @return
     */
    @GetMapping(value = "admin/getByPid/{pid}")
    @ApiOperation(value = "通过项目id获取项目的成员信息表, 管理员进行管理的时候使用", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", required = true, value = "项目的id", dataType = "int", paramType = "path")
    })
    public List<Collaborators> getByPid(@PathVariable(value = "pid")int pid){
        return collaboratorsService.getByPid(pid);
    }


    /**
     * 通过用户的id获取这个用户所在的项目成员信息列表
     * @param uid
     * @return
     */
    @GetMapping(value = "getByUId/{uid}")
    @ApiOperation(value = "通过用户的id获取这个用户所在的项目成员信息列表， 用户查看自己参与的项目的时候使用", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<Collaborators> getByUid(@PathVariable(value = "uid")int uid){
        return collaboratorsService.getByUid(uid);
    }



    @PostMapping(value = "update")
    @ApiOperation(value = "用户添加一个项目中的自己的项目的链接地址， 和自己的github名称", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "项目成员列表的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "url", required = true, value = "用户的项目地址： username:projectname , 例如：BirdWong/schoolsystem。不要协议头（http） 域名（github） 也不要以'/'开头或者'/'结尾", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "name", required = true, value = "用户的github名称。 作用： 如果一个项目是两个人开发， 那么就要根据项目中提交人的名称得到他的提交记录", dataType = "string", paramType = "query")
    })
    public MessageResult updateUrl(HttpServletRequest req, @RequestBody Collaborators collaborators){
        if (CheckUserUtil.isUser(req, collaborators.getUid())) {
            String url = collaborators.getUrl();
            String[] urls = url.split("/");
            if (urls.length != 2) {
                return new MessageResult(false, "url填写错误");
            }
            url = "https://api.github.com/repos/" + url + "/commits";
            return collaboratorsService.updateUrl(collaborators.getId(), collaborators.getUid(), url, collaborators.getName());
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }



}
