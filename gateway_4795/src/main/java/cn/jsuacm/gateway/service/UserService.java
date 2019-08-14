package cn.jsuacm.gateway.service;

import cn.jsuacm.gateway.pojo.User;
import cn.jsuacm.gateway.pojo.enity.MessageResult;
import cn.jsuacm.gateway.pojo.enity.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName UserService
 * @Description 用户的服务层接口
 * @Author h4795
 * @Date 2019/06/17 16:28
 */
public interface UserService extends IService<User> {

    /**
     * 发送注册邮件
     * @param email 邮箱
     * @return
     */
    public MessageResult sendRegisterEmail(String email);


    /**
     * 用户注册
     * @param user 用户信息
     * @return
     */
    public MessageResult registerUser(User user,String code);


    /**
     *
     * @param email 邮箱账户
     * @param code 验证码
     * @return
     */
    public boolean checkEmailCode(String email, String code);


    /**
     * 找回密码发送邮件
     * @param email
     * @return
     */
    public MessageResult sendUpdateEmail(String email);

    /**
     * 通过验证邮箱更改密码
     * @param newPwd 新的密码
     * @param email 用户的email
     * @param code 验证码
     * @return
     */
    public MessageResult updatePasswordByEmail(String email, String newPwd, String code);


    /**
     * 通过旧密码修改密码
     * @param oldPwd 旧的密码
     * @param newPwd 新的密码
     * @param uid 用户的id
     * @return
     */
    public MessageResult updatePassword(int uid, String oldPwd, String newPwd);


    /**
     * 修改用户的用户名
     * @param uid 用户的id
     * @param name 新的用户名
     * @return
     */
    public MessageResult updateUsername(int uid, String name);


    /**
     * 修改用户的头像链接
     * @param uid 用户的id
     * @param url 用户的头像链接
     * @return
     */
    public MessageResult updatePicUrl(int uid, String url);

    /**
     * 修改用户的邮箱
     * @param uid 用户的id
     * @param email 用户的邮箱
     * @return
     */
    public MessageResult updateEmail(int uid, String email, String code);

    /**
     * 通过账号获取用户信息
     * @param accountNumber 账户信息
     * @return
     */
    public User getUser(String accountNumber);


    /**
     * 管理员直接修改密码
     * @param uid 用户id
     * @param newPwd 新的密码
     * @return
     */
    public MessageResult updatePassword(int uid, String newPwd);


    /**
     * 分页获取用户所有信息以及权限
     * @param row
     * @param pageSize
     * @return
     */
    public PageResult<UserAuthentication> getUserAuthentication(int row, int pageSize);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class UserAuthentication{
        private int uid;
        private String accountNumber;
        private String email;
        private List<String> roles;
    }


    /**
     * 判断是否有这个用户，用于给其他模块验证是否存在用户
     * @param uid 用户的id
     * @return
     */
    public boolean isUser(int uid);


    /**
     * 获取实验室成员
     * @return
     */
    public List<User> getCcwMenber();


    /**
     * 获取正在实验室的成员
     * @return
     */
    public List<User> getCcwIngMenber();

}
