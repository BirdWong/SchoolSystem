package cn.jsuacm.ccw.controller.project;

import cn.jsuacm.ccw.pojo.projects.Commit;
import cn.jsuacm.ccw.service.projects.CommitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName CommitController
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/02 17:07
 */
@RestController
@RequestMapping(value = "commit")
@Api(value = "提交信息查看操作， 添加修改是自动获取")
public class CommitController {

    @Autowired
    CommitService commitService ;


    /**
     * 通过成员信息列表获取这个成员的提交记录
     * @param cid 项目成员信息id
     * @return
     */
    @GetMapping(value = "getByCid/{cid}")
    @ApiOperation(value = "通过成员信息列表获取这个成员的提交记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value = "成员信息列表id", dataType = "int", paramType = "path")
    })
    public List<Commit> getByCid(@PathVariable(value = "cid") int cid){
        return commitService.findByCid(cid);
    }
}
