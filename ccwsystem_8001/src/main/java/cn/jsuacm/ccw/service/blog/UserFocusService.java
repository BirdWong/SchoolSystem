package cn.jsuacm.ccw.service.blog;

import cn.jsuacm.ccw.pojo.blog.UserFocus;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @ClassName UserFocusService
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/28 21:09
 */
public interface UserFocusService extends IService<UserFocus> {


    /**
     * 添加一个关注信息
     * @param userFocus
     * @return
     */
    public MessageResult addFocus(UserFocus userFocus);


    /**
     * 通过关注id删除一条信息
     * @param id 此条信息的id
     * @return
     */
    public MessageResult deleteFocusById(int id, int uid);


    /**
     * 删除uid关注的所有信息
     * @param uid 用户id
     * @return
     */
    public MessageResult deleteFocusByFromUid(int uid);


    /**
     * 删除所有被uid关注的信息
     * @param uid 用户id
     * @return
     */
    public MessageResult deleteFocusByToUid(int uid);


    /**
     * 删除uid的所有信息， 无论是关注或者被关注
     * @param uid 用户id
     * @return
     */
    public MessageResult deleteAllFocusByUid(int uid);


    /**
     * 得到uid的关注的列表
     * @param uid 用户id
     * @return
     */
    public List<UserFocus> getFocusList(int uid);


    /**
     * 得到uid的粉丝列表
     * @param uid 用户id
     * @return
     */
    public List<UserFocus> getFansList(int uid);


    /**
     * 得到uid的关注数量
     * @param uid 用户id
     * @return
     */
    public MessageResult getFocusCount(int uid);


    /**
     * 得到uid的粉丝数量
     * @param uid 用户id
     * @return
     */
    public MessageResult getFansCount(int uid);
}
