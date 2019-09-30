package cn.jsuacm.ccw.service.blog.impl;

import cn.jsuacm.ccw.mapper.blog.UserFocusMapper;
import cn.jsuacm.ccw.pojo.blog.UserFocus;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.blog.UserFocusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @ClassName UserFocusServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/28 21:08
 */
@Service

public class UserFocusServiceImpl extends ServiceImpl<UserFocusMapper, UserFocus> implements UserFocusService {

    @Autowired
    private UserFocusMapper userFocusMapper;


    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate restTemplate;


    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/isUser/";


    /**
     * 添加一个关注信息
     *
     * @param userFocus
     * @return
     */
    @Override
    public MessageResult addFocus(UserFocus userFocus) {
        int toUid = userFocus.getToUid();

        ResponseEntity<String> entity = restTemplate.getForEntity(url + String.valueOf(toUid), String.class);

        if ("true".equals(entity.getBody())){
            QueryWrapper<UserFocus> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("from_uid", userFocus.getFromUid()).eq("to_uid", userFocus.getToUid());
            Integer count = userFocusMapper.selectCount(queryWrapper);
            if (count != 0){
                return new MessageResult(false, "已经存在信息");
            }else {
                save(userFocus);
                return new MessageResult(true, "关注成功");
            }
        }
        return new MessageResult(false, "被关注的对象不存在");
    }

    /**
     * 通过关注id删除一条信息
     *
     * @param id 此条信息的id
     * @return
     */
    @Override
    public MessageResult deleteFocusById(int id, int uid) {
        QueryWrapper<UserFocus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        queryWrapper.select("from_uid");
        UserFocus userFocus = userFocusMapper.selectOne(queryWrapper);
        if (userFocus != null && userFocus.getFromUid() == uid){
            removeById(id);
            return new MessageResult(true, "删除成功");
        }
        return new MessageResult(false, "这条信息不属于这个用户");
    }

    /**
     * 删除uid关注的所有信息
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public MessageResult deleteFocusByFromUid(int uid) {
        UpdateWrapper<UserFocus>  updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("from_uid", uid);
        remove(updateWrapper);
        return new MessageResult(true, "删除成功");
    }

    /**
     * 删除所有被uid关注的信息
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public MessageResult deleteFocusByToUid(int uid) {
        UpdateWrapper<UserFocus>  updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("to_uid", uid);
        remove(updateWrapper);
        return new MessageResult(true, "删除成功");
    }

    /**
     * 删除uid的所有信息， 无论是关注或者被关注
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public MessageResult deleteAllFocusByUid(int uid) {
        deleteFocusByFromUid(uid);
        deleteFocusByToUid(uid);
        return new MessageResult(true, "删除成功");
    }

    /**
     * 得到uid的关注的列表
     *
     * @param uid 用户id
     * @return
     */
    @Override

    public List<UserFocus> getFocusList(int uid) {
        QueryWrapper<UserFocus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("from_uid", uid);
        return userFocusMapper.selectList(queryWrapper);
    }

    /**
     * 得到uid的粉丝列表
     *
     * @param uid 用户id
     * @return
     */
    @Override

    public List<UserFocus> getFansList(int uid) {
        QueryWrapper<UserFocus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_uid", uid);
        return userFocusMapper.selectList(queryWrapper);
    }

    /**
     * 得到uid的关注数量
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public MessageResult getFocusCount(int uid) {
        QueryWrapper<UserFocus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("from_uid", uid);
        return new MessageResult(true, userFocusMapper.selectCount(queryWrapper)+"");
    }

    /**
     * 得到uid的粉丝数量
     *
     * @param uid 用户id
     * @return
     */
    @Override

    public MessageResult getFansCount(int uid) {
        QueryWrapper<UserFocus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_uid", uid);
        return new MessageResult(true, userFocusMapper.selectCount(queryWrapper)+"");    }
}
