package cn.jsuacm.ccw.service.blog.impl;

import cn.jsuacm.ccw.mapper.blog.LabelMapper;
import cn.jsuacm.ccw.pojo.blog.Label;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.blog.ArticleInfomationService;
import cn.jsuacm.ccw.service.blog.LabelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @ClassName LabelServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/22 16:12
 */
@Service

public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements LabelService{


    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate template;


    @Autowired
    private LabelMapper labelMapper;


    @Autowired
    private ArticleInfomationService articleInfomationService;


    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/isUser/";

    /**
     * 添加标签
     *
     * @param label
     * @return
     */
    @Override
    public MessageResult addLabel(Label label) {

        // 1. 校验信息
        if (!checkLabel(label)){
            return new MessageResult(false, "信息错误");
        }

        // 2. 判断是否已经有这个标签
        QueryWrapper<Label> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", label.getUid()).eq("lname", label.getLname());
        Integer count = labelMapper.selectCount(wrapper);
        if (count != 0){
            return new MessageResult(false, "该用户已经有这个标签");
        }
        // 3. 保存这个标签，然后返回标签的id
        save(label);
        return new MessageResult(true, label.getLid() + "");
    }

    /**
     * 修改标签
     *
     * @param label
     * @return
     */
    @Override
    public MessageResult updateLable(Label label) {

        if(!checkLabel(label)){
            return new MessageResult(false, "信息错误");
        }

        QueryWrapper<Label>  wrapper = new QueryWrapper<>();

        wrapper.eq("uid", label.getUid()).eq("lid", label.getLid());

        Integer count = labelMapper.selectCount(wrapper);
        if (count == 0){
            return new MessageResult(false, "没有创建过这个标签");
        }
        updateById(label);

        return new MessageResult(true, "修改成功");
    }


    /**
     * 删除标签
     *
     * @param lid
     * @return
     */
    @Override
    public MessageResult delteLable(int lid) {
        Label label = getById(lid);
        articleInfomationService.deleteAllByLid(label.getUid(), lid);
        removeById(lid);
        return new MessageResult(true, "删除成功");
    }

    /**
     * 通过uid获取一个用户的所有标签
     *
     * @param uid
     * @return
     */
    @Override

    public List<Label> getByUid(int uid) {
        QueryWrapper<Label> wrapper = new QueryWrapper<>();

        wrapper.eq("uid", uid);

        List<Label> labels = labelMapper.selectList(wrapper);


        return labels;
    }

    /**
     * 获取所有的用户的标签
     *
     * @return
     */
    @Override

    public Map<Integer, List<Label>> getLabelList() {
        List<Label> labels = labelMapper.selectList(null);
        Map<Integer, List<Label>> labelMap = new HashMap<>();
        for (Label label : labels){
            if (labelMap.containsKey(label.getUid())){
                List<Label> labelList = labelMap.get(label.getUid());
                labelList.add(label);
                labelMap.put(label.getUid(),labelList);
            }else {
                List<Label> labelList = new ArrayList<>();
                labelList.add(label);
                labelMap.put(label.getUid(), labelList);
            }
        }
        return labelMap;
    }

    /**
     * 检测这些是否都有，并且是这个用户的
     *
     * @param ids
     * @return
     */
    @Override
    public boolean checkCount(Integer[] ids) {
        QueryWrapper<Label> wrapper = new QueryWrapper<>();
        wrapper.in("lid", Arrays.asList(ids));
        List<Label> labels = labelMapper.selectList(wrapper);
        if (labels.size() == 0){
            return false;
        }
        int uid = labels.get(0).getUid();
        for (Label label : labels){
            if (label.getUid() != uid){
                return false;
            }
        }

        return true;
    }


    /**
     * 校验填写内容是否完整
     * @param label
     * @return
     */
    private  boolean checkLabel(Label label){
        if (label.getLname() == null || "".equals(label.getLname())){
            return false;
        }

        ResponseEntity<String> entity = template.getForEntity(url + String.valueOf(label.getUid()), String.class);
        // 校验用户
        if (!"true".equals(entity.getBody())){
            return false;
        }

        return  true;
    }
}
