package cn.jsuacm.ccw.service.recruit;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.recruit.FormItemOption;

import java.util.List;

/**
 * @ClassName FormService
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/07 14:56
 */
public interface FormService {


    /**
     * 表单中在redis的键值
     */
    public static final String FORM_KEY = "form_key";


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
    public MessageResult addForm(List<FormItemOption> formItemOptions);


    /**
     * 获取表单组件
     * @return
     */
    public List<FormItemOption> getForm();


    /**
     * 更新表单
     * @param formItemOptions
     * @return
     */
    public MessageResult updateForm(List<FormItemOption> formItemOptions);


    /**
     * 删除表单
     * @return
     */
    public MessageResult deleteForm();


    /**
     * 判断是否有表单
     * @return
     */
    public boolean hasForm();
}
