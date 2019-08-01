package cn.jsuacm.ccw.service.blog;

import cn.jsuacm.ccw.pojo.blog.Label;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName LabelService
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/22 16:12
 */
public interface LabelService extends IService<Label>{

    /**
     * 添加标签
     * @param label
     * @return
     */
    public MessageResult addLabel(Label label);


    /**
     * 修改标签
     * @param label
     * @return
     */
    public MessageResult updateLable(Label label);


    /**
     * 删除标签
     * @param lid
     * @return
     */
    public MessageResult delteLable(int lid);

    /**
     * 通过uid获取一个用户的所有标签
     * @param uid
     * @return
     */
    public List<Label> getByUid(int uid);


    /**
     * 获取所有的用户的标签
     * @return
     */
    public Map<Integer, List<Label>> getLabelList();

    /**
     * 检测这些是否都有，并且是这个用户的
     * @param ids
     * @return
     */
    public boolean checkCount(Integer[] ids);

}
