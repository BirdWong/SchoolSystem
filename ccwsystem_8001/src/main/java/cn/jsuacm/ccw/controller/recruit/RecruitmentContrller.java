package cn.jsuacm.ccw.controller.recruit;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.recruit.NewMenberInfo;
import cn.jsuacm.ccw.service.recruit.RecruitmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * @ClassName RecruitmentContrller
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/12 15:23
 */
@RestController
@RequestMapping(value = "recruitment")
@Api(value = "recruitment", description = "报名信息的操作")
public class RecruitmentContrller {

    @Autowired
    private RecruitmentService recruitmentService;


    /**
     * 新生添加自己的报名信息, 如果已经有相同报名的邮箱，那么认为已经报名了
     * @param newMenberInfo
     * @return
     */
    @PostMapping(value = "add")
    @ApiOperation(value = "用户报名", notes = "新生添加自己的报名信息, 如果已经有相同报名的邮箱，那么认为已经报名了", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "报名者的姓名", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "email", required = true, value = "报名者的邮箱", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "qq", required = true, value = "报名者的qq", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pictrue", required = true, value = "报名者的照片base64", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sex", required = true, value = "报名者的性别， 要么是男要么是女， 如果值不是 男or女 则直接报错", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "from", required = true, value = "报名者的班级， 只能是数字", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "content", required = true, value = "报名者的自我介绍，最长限制1000字， 不能不填写", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "age", required = true, value = "报名者的年龄， 不得小于15，不得大于30", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "obj", required = true, value = "报名者的其他资料信息", dataType = "string", paramType = "query")
    })
    public MessageResult add(@RequestBody NewMenberInfo newMenberInfo){
        return recruitmentService.add(newMenberInfo);
    }


    /**
     * 获取按照班级的用户报名信息， 按照班级数字的大小排序
     * @return
     */
    @GetMapping(value = "getToClass")
    @ApiOperation(value = "获取所有的报名信息，并且按照班级依次排序", notes = "获取按照班级的用户报名信息， 按照班级数字的大小排序", httpMethod = "GET")
    public TreeMap<Integer, List<NewMenberInfo>> getToClass(){
        return recruitmentService.getToClass();
    }


    /**
     * 通过班级数字获取这个班级的报名人信息列表
     * @param num 班级数字
     * @return
     */
    @GetMapping(value = "getByClass/{num}")
    @ApiOperation(value = "通过班级数字获取这个班级的报名人信息列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", required = true, value = "班级数字", dataType = "int", paramType = "path")
    })
    public List<NewMenberInfo> getByClass(@PathVariable(value = "num") int num){
        return recruitmentService.getByClass(num);
    }


    /**
     * 获取报名的班级， 通过这个接口可以获取到有哪些班级有人报名
     * @return
     */
    @GetMapping(value = "getClassNumbers")
    @ApiOperation(value = "获取报名的班级", notes = "获取报名的班级， 通过这个接口可以获取到有哪些班级有人报名", httpMethod = "GET")
    public List<Integer> getClassNumbers(){
        return recruitmentService.getClassNumbers();
    }


    /**
     * 管理员更新报名者的信息, 用途可以修改用户的基础信息，同时也可以在最开始的时候添加心理测试的组件， 然后进行心理测试后管理员将心理测试结果回填
     * @param newMenberInfo
     * @return
     */
    @PostMapping("admin/update")
    @ApiOperation(value = "更新报名者的信息", notes = "管理员更新报名者的信息, 用途可以修改用户的基础信息，同时也可以在最开始的时候添加心理测试的组件， 然后进行心理测试后管理员将心理测试结果回填", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", required = true, value = "报名者的姓名", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "email", required = true, value = "报名者的邮箱", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "qq", required = true, value = "报名者的qq", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "pictrue", required = true, value = "报名者的照片base64", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sex", required = true, value = "报名者的性别， 要么是男要么是女， 如果值不是 男or女 则直接报错", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "from", required = true, value = "报名者的班级， 只能是数字", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "content", required = true, value = "报名者的自我介绍，最长限制1000字， 不能不填写", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "age", required = true, value = "报名者的年龄， 不得小于15，不得大于30", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "obj", required = true, value = "报名者的其他资料信息", dataType = "string", paramType = "query")
    })
    public MessageResult updateInfo(@RequestBody NewMenberInfo newMenberInfo){
        return recruitmentService.updateInfo(newMenberInfo);
    }


    /**
     * 通过报名者的邮箱获取这个报名者的信息, 没有这个报名表，则返回为空
     * @param email 用户的邮箱
     * @return
     */
    @GetMapping(value = "getByEmail/{email}")
    @ApiOperation(value = "通过报名者的邮箱获取这个报名者的信息", notes = "通过报名者的邮箱获取这个报名者的信息, 没有这个报名表，则返回为空", httpMethod = "GET")
    public NewMenberInfo getByEmail(@PathVariable(value = "email")String email){
        return recruitmentService.getByEmail(email);
    }


    /**
     * 下载报名者的面试分数信息，并且按照平均分从高到底排序
     * @param response
     */
    @GetMapping(value = "admin/download")
    @ApiOperation(value = "下载报名者的面试分数信息", notes = "下载报名者的面试分数信息，并且按照平均分从高到底排序", httpMethod = "GET")
    public void download(HttpServletResponse response){
        recruitmentService.download(response);
    }


    /**
     * 面试打分，分数范围10-100, 分数可以是小数，建议只有两位， 如果超出两位小位数，后台会自动截取， 如果已经打分了那么再次打分就会覆盖之前的打分
     * @param email 面试者的邮箱
     * @param uid 打分者的id
     * @param score 分数， 10-100， 可以有小数， 并且如果超出两位小数会后台自动截取
     * @return
     */
    @GetMapping(value = "setSorce/{email}/{uid}/{score}")
    @ApiOperation(value = "面试打分", notes = "面试打分，分数范围10-100, 分数可以是小数，建议只有两位， 如果超出两位小位数，后台会自动截取， 如果已经打分了那么再次打分就会覆盖之前的打分", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", required = true, value = "面试者的邮箱", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "uid", required = true, value = "打分者的id", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "score", required = true, value = "分数， 10-100， 可以有小数， 并且如果超出两位小数会后台自动截取", dataType = "double", paramType = "path")
    })
    public MessageResult setSorce(@PathVariable(value = "email") String email, @PathVariable(value = "uid") String uid, @PathVariable(value = "score") double score){
        return recruitmentService.setScore(email, uid, score);
    }


    /**
     * 上传图片转换成base64字符
     * @param multipartFile
     * @return
     */
    @PutMapping(value = "upload")
    @ApiOperation(value = "上传图片转换成base64", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "img", required = true, value = "图片文件", dataType = "file", paramType = "query")
    })
    public MessageResult uploadPicture(@RequestParam("img") MultipartFile multipartFile){
        return recruitmentService.uploadPictrue(multipartFile);
    }



    @PutMapping(value = "admin/sendEmail")
    @ApiOperation(value = "报名截止后发送面试通知", notes = "报名截止后发送面试时间、 提示通知, 提示不是必填，可以为空字符串", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", required = true, value = "面试的时间", dataType = "date", paramType = "query"),
            @ApiImplicitParam(name = "address", required = true, value = "面试的地点", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "msg", value = "给其他面试者的提示信息，例如带纸笔", dataType = "string", paramType = "query")
    })
    public MessageResult sendEmail(@RequestParam(value = "date") Date date, @RequestParam(value = "address") String address, @RequestParam(value = "msg") String msg){
        return recruitmentService.sendEmail(date, address, msg);
    }


}
