package cn.jsuacm.ccw.service.recruit.impl;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.recruit.FormItemOption;
import cn.jsuacm.ccw.service.recruit.FormService;
import cn.jsuacm.ccw.service.recruit.LimitTimerService;
import cn.jsuacm.ccw.service.recruit.RecruitmentService;
import cn.jsuacm.ccw.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FormServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/07 15:00
 */
@Service
public class FormServiceImpl implements FormService {


    @Autowired
    private LimitTimerService limitTimerService;



    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * <p>
     *     添加表单组件， 如果已经<strong>存在</strong>, 则添加失败
     * </p>
     * <p>
     *     添加过程中如果出现了添加失败等问题， 有可能是
     *     <ul>
     *         <li>
     *             已经存在表单在redis中
     *         </li>
     *         <li>
     *             redis服务被关闭
     *         </li>
     *         <li>
     *             redis连接池饱满
     *         </li>
     *     </ul>
     * </p>
     *
     *
     * @param formItemOptions
     * @return
     */
    @Override
    public MessageResult addForm(List<FormItemOption> formItemOptions) {
        boolean hasRecruitment = recruitmentService.hasRecruitment();
        boolean hasForm = hasForm();
        if (hasForm){
            return new MessageResult(false, "已经存在表单， 删除后再添加");
        }else if(hasRecruitment){
            return new MessageResult(false, "已经存在报名表, 无法添加");
        }else{
            boolean set = redisUtil.set(FormService.FORM_KEY, formItemOptions);
            return new MessageResult(set, "添加成功");
        }
    }

    /**
     * 获取表单组件
     *
     * @return
     */
    @Override
    public List<FormItemOption> getForm() {
        if (hasForm()){
            return (List<FormItemOption>) redisUtil.get(FormService.FORM_KEY);
        }
        return new ArrayList<>();
    }

    /**
     * 更新表单
     *
     * @param formItemOptions
     * @return
     */
    @Override
    public MessageResult updateForm(List<FormItemOption> formItemOptions) {
        if (limitTimerService.hasTime()){
            return new MessageResult(false, "已经开始报名， 不允许修改");
        }
        if (recruitmentService.hasRecruitment()){
            return new MessageResult(false, "已经存在报名表，不允许修改");
        }
        deleteForm();
        redisUtil.set(FormService.FORM_KEY, formItemOptions);
        return new MessageResult(true, "更新成功");
    }

    /**
     * 删除表单
     *
     * @return
     */
    @Override
    public MessageResult deleteForm() {
        if (limitTimerService.hasTime()){
            return new MessageResult(false, "已经开始报名， 不允许删除");
        }
        if (recruitmentService.hasRecruitment()){
            return new MessageResult(false, "已经存在报名表，不允许删除");
        }
        if (hasForm()){
            redisUtil.del(FormService.FORM_KEY);
            return new MessageResult(true, "删除成功");
        }
        return new MessageResult(false, "没有表单信息");
    }


    /**
     * 判断是否有表单
     * @return
     */
    @Override
    public boolean hasForm() {
        return redisUtil.hasKey(FormService.FORM_KEY);
    }
}
