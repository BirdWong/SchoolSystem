package cn.jsuacm.ccw.controller.project;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.pojo.projects.Project;
import cn.jsuacm.ccw.service.projects.ProjectService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ProjectController
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 19:58
 */
@RestController
@RequestMapping("/project")
@Api(value = "project", description = "项目操作，status：1 正在进行   -1： 结束")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    /**
     * 保存一个项目信息
     * @param project
     * @return
     */
    @PostMapping(value = "admin/add")
    @ApiOperation(value = "添加一个项目信息", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "项目名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "description", required = true, value = "项目描述markdown", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "htmlDescription", required = true, value = "项目描述的html", dataType = "string", paramType = "query")
    })
    public MessageResult addProject(@RequestBody Project project){
        return projectService.addProject(project);
    }


    /**
     * 分页获取所有项目
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/getPages/{current}/{pageSize}")
    @ApiOperation(value = "分页获取项目列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小", paramType = "path")
    })
    public PageResult<Project> getPages(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize")int pageSize){
        return projectService.getPages(0, current, pageSize);
    }


    /**
     * 分页获取所有正在进行的项目
     * @param current
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页获取所有正在进行的项目", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小", paramType = "path")
    })
    @GetMapping(value = "/getOpenPages/{current}/{pageSize}")
    public PageResult<Project> getOpenPages(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize")int pageSize){
        return projectService.getPages(Project.PROJECT_OPEN, current, pageSize);
    }




    /**
     * 分页获取所有结束的项目
     * @param current
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页获取所有结束的项目", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小", paramType = "path")
    })
    @GetMapping(value = "/getClosePages/{current}/{pageSize}")
    public PageResult<Project> getClosePages(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize")int pageSize){
        return projectService.getPages(Project.PROJECT_CLOSE, current, pageSize);
    }


    /**
     * 更新项目的描述等信息
     * @param project
     * @return
     */
    @PostMapping(value = "admin/updateInfo")
    @ApiOperation(value = "更新项目的信息， 只允许修改项目名称，项目的描述，其他信息不允许修改", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "项目名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "description", required = true, value = "项目描述", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "htmlDescription", required = true, value = "项目描述的html", dataType = "string", paramType = "query")

    })
    public MessageResult updateProject(@RequestBody Project project){
        return projectService.updateProject(project);
    }


    /**
     * 更新项目的状态， 由开启到关闭，关闭到重新开启
     * @param id 项目的id
     * @return
     */
    @GetMapping(value = "admin/updateStatus")
    @ApiOperation(value = "更新项目的状态， 由开启到关闭/关闭到重新开启", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "项目的id", dataType = "int", paramType = "path")
    })
    public MessageResult updateStatus(@PathVariable(value = "id") int id){
        return projectService.updateStatus(id);
    }


    /**
     * 删除一个项目
     * @param id
     * @return
     */
    @GetMapping("admin/delete/{id}")
    @ApiOperation(value = "删除一个项目", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "项目id", dataType = "int", paramType = "path")
    })
    public MessageResult delete(@PathVariable(value = "id") int id){
        return projectService.deleteById(id);
    }
}
