package cn.jsuacm.ccw.service.recruit.impl;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.recruit.FormService;
import cn.jsuacm.ccw.service.recruit.LimitTimerService;
import cn.jsuacm.ccw.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName LimitTimerServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/10 16:10
 */

@Service
public class LimitTimerServiceImpl implements LimitTimerService {

    @Autowired
    private FormService formService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 开始报名
     * <p>
     * <ul>
     * <li>拥有表单信息</li>
     * <li>如果报名开始了，就不可以修改表单</li>
     * </ul>
     *
     * @param len 报名的持续开放时间
     * @return
     */
    @Override
    public MessageResult begin(Long len) {
        if (hasTime()){
            return new MessageResult(false, "已经开始报名");
        }
        if (formService.hasForm()){
            if (len <= 0){
                return new MessageResult(false, "剩余时间设置错误");
            }
            redisUtil.set(BEGIN_TIME, new Date(), len);
            return new MessageResult(true, "设置成功");
        }else {
            return new MessageResult(false, "请先创建表单");
        }
    }

    /**
     * 修改报名剩余的时间
     *
     * @param len 报名持续开放时间
     * @return
     */
    @Override
    public MessageResult update(Long len) {
        if (len <= 0){
            return new MessageResult(false, "剩余时间设置错误");
        }
        if (hasTime()){
            redisUtil.expire(BEGIN_TIME, len);
            return  new MessageResult(true, "修改成功");
        }else {
            return new MessageResult(false, "报名结束,不允许修改");
        }
    }



    /**
     * 获取报名的剩余时间以及开启时间
     *
     * @return
     */
    @Override
    public MessageResult get() {
        if (hasTime()){
            Date date = (Date) redisUtil.get(BEGIN_TIME);
            long expire = redisUtil.getExpire(BEGIN_TIME);
            return new MessageResult(true, date.toString() + ","+ expire);
        }else {
            return new MessageResult(false, "报名已经结束");
        }
    }

    /**
     * 关闭报名
     *
     * @return
     */
    @Override
    public MessageResult close() {
        if (hasTime()){
            redisUtil.del(BEGIN_TIME);
            return new MessageResult(true, "关闭成功");
        }else {
            return new MessageResult(false, "报名已经结束");
        }
    }

    /**
     * 确认是否已经开始报名
     * @return
     */
    @Override
    public boolean hasTime() {
        return redisUtil.hasKey(BEGIN_TIME);
    }

}
