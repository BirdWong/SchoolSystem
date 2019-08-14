package cn.jsuacm.ccw.controller.recruit;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.recruit.LimitTimerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName LimitTimerController
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/12 14:48
 */
@RestController
@RequestMapping(value = "limitTime")
@Api(value = "limitTime", description = "报名开始和报名时间设置")
public class LimitTimerController {



    @Autowired
    private LimitTimerService limitTimerService;


    /**
     * 开始招新报名， 设置的时间到达后截止开始报名
     * @param time  报名的开放持续时间， 单位秒，例如：开放时间一天 time=24*60*60
     * @return
     */
    @PutMapping(value = "admin/begin")
    @ApiOperation(value = "开始报名", notes = "开始招新报名， 设置的时间到达后截止开始报名", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", required = true, value = "报名的开放持续时间， 单位秒，例如：开放时间一天 time=24*60*60", dataType = "long", paramType = "query")
    })
    public MessageResult beginRecruitment(@RequestParam(value = "time")Long time){
        return limitTimerService.begin(time);
    }


    /**
     * 更新剩余的时间， 例如： 目前剩余报名时间还剩下30s， 通过update 传入参数 500， 则结束时间就是还剩下500s， 通过这个方法可以减少更新的时间， 也可以增加更新的时间, 这个接口只有在开放招新的时间内有效，一旦时间已经结束是不可以重新开放的
     * @param time 更新报名的开放持续时间， 单位秒，例如：开放时间一天 time=24*60*60
     * @return
     */
    @PutMapping(value = "admin/update")
    @ApiOperation(value = "更新剩余的报名时间", notes = "更新剩余的时间， 例如： 目前剩余报名时间还剩下30s， 通过update 传入参数 500， 则结束时间就是还剩下500s， 通过这个方法可以减少更新的时间， 也可以增加更新的时间, 这个接口只有在开放招新的时间内有效，一旦时间已经结束是不可以重新开放的", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", required = true, value = "更新报名的开放持续时间， 单位秒，例如：开放时间一天 time=24*60*60", dataType = "long", paramType = "query")
    })
    public MessageResult updateRecruitmentTime(@RequestParam(value = "time")Long time){
        return limitTimerService.update(time);
    }


    /**
     * 获取报名的开始时间和剩余时间， 如果获取成功， 则message信息里面会将开始时间和剩余时间返回，以','分割： Mon Aug 12 15:13:16 CST 2019,1000, 前面的时间为开始的时间， 结束剩余的时间为1000秒
     * @return
     */
    @GetMapping(value = "get")
    @ApiOperation(value = "获取报名开始的时间和结束的时间", notes = "获取报名的开始时间和剩余时间， 如果获取成功， 则message里面会将开始时间和剩余时间返回，以','分割： Mon Aug 12 15:13:16 CST 2019,1000, 前面的时间为开始的时间， 结束剩余的时间为1000秒", httpMethod = "GET")
    public MessageResult get(){
        return limitTimerService.get();
    }


    /**
     * 提前关闭报名
     * @return
     */
    @GetMapping(value = "close")
    @ApiOperation(value = "提前关闭报名", notes = "提前关闭报名， 不管是否还有时间都会结束报名， 但是返回状态 true和false 提示信息不一样", httpMethod = "GET")
    public MessageResult close(){
        return limitTimerService.close();
    }
}
