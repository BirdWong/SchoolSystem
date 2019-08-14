package cn.jsuacm.ccw.service.recruit;

import cn.jsuacm.ccw.pojo.enity.MessageResult;

import java.util.Date;

/**
 * @ClassName LimitTimerService
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/10 15:59
 */
public interface LimitTimerService {


    public static final String BEGIN_TIME = "begin_time";

    /**
     * 开始报名
     *
     *      <ul>
     *          <li>拥有表单信息</li>
     *          <li>如果报名开始了，就不可以修改表单</li>
     *      </ul>
     * @param len 报名的持续开放时间
     * @return
     */
    public MessageResult begin(Long len);

    /**
     * 修改报名剩余的时间
     * @param len  报名持续开放时间
     * @return
     */
    public MessageResult update(Long len);


    /**
     * 获取报名的剩余时间以及开启时间
     * @return
     */
    public MessageResult get();


    /**
     * 关闭报名
     * @return
     */
    public MessageResult close();


    /**
     * 确认是否已经开始报名
     * @return
     */
    public boolean hasTime();
}
