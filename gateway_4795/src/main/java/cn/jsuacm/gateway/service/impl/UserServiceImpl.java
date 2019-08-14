package cn.jsuacm.gateway.service.impl;

import cn.jsuacm.gateway.mapper.AuthenticationMapper;
import cn.jsuacm.gateway.mapper.UserMapper;
import cn.jsuacm.gateway.pojo.Authentication;
import cn.jsuacm.gateway.pojo.User;
import cn.jsuacm.gateway.pojo.enity.MessageResult;
import cn.jsuacm.gateway.pojo.enity.PageResult;
import cn.jsuacm.gateway.service.UserService;
import cn.jsuacm.gateway.util.EmailUtil;
import cn.jsuacm.gateway.util.MD5Util;
import cn.jsuacm.gateway.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description 用户服务层实现
 * @Author h4795
 * @Date 2019/06/17 16:30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private AuthenticationMapper authenticationMapper;


    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return
     */
    @Override
    public MessageResult sendRegisterEmail(String email) {
        if (checkEmail(email)){
            // 如果有这个在一分钟之内发送过信息
            if (redisUtil.hasKey(email)){
                return new MessageResult(false, String.valueOf(redisUtil.getExpire(email)));
            }
            if (redisUtil.hasKey("reg_"+email)){
                redisUtil.del("reg_"+email);
            }
            // 生成随机码
            String random = RandomStringUtils.random(6, false, true);
            MessageResult messageResult = EmailUtil.sendBindingMail(email, random);
            redisUtil.set("reg_"+email, random, EmailUtil.EMAIL_TIME);
            redisUtil.set(email, 1, EmailUtil.SIGN_TIME);
            return messageResult;
        }
        return new MessageResult(false, "邮箱已经被注册");
    }

    @Override
    public MessageResult registerUser(User user, String code) {
        if (!checkEmailCode("reg_"+user.getEmail(), code)){
            return new MessageResult(false, "验证码错误");
        }
        if (checkUser(user)) {
            if(!checkEmail(user.getEmail())){
                return new MessageResult(false,"邮箱已经被注册");
            }
            if (!checkAccountNUmber(user.getAccountNumbser())){
                return new MessageResult(false, "用户名已经被注册");
            }
            // 密码转换md5后再次加密
            user.setPassword(MD5Util.convertMD5(MD5Util.string2MD5(user.getPassword())));
            userMapper.insert(user);
            return new MessageResult(true, "注册成功");
        }
        return new MessageResult(false, "注册信息或者验证码未正确填写");
    }


    /**
     * 检查
     * @param email 邮箱账户
     * @param code 验证码
     * @return
     */
    @Override
    public boolean checkEmailCode(String email, String code) {
        if (redisUtil.hasKey(email)){
            String acode = String.valueOf(redisUtil.get(email));
            if (acode.equals(code)){
                redisUtil.del(email);
                return true;
            }
        }
        return false;
    }

    /**
     * 发送信息修改邮件
     *
     * @param email
     * @return
     */
    @Override
    public MessageResult sendUpdateEmail(String email) {


        if (!checkEmail(email)){
            // 如果有这个在一分钟之内发送过信息
            if (redisUtil.hasKey(email)){
                return new MessageResult(false, String.valueOf(redisUtil.getExpire(email)));
            }
            if (redisUtil.hasKey("upd_"+email)){
                redisUtil.del("upd_"+email);
            }
            // 生成随机码
            String random = RandomStringUtils.random(6, false, true);
            MessageResult messageResult = EmailUtil.sendUpdataEmail(email, random);
            redisUtil.set("upd_"+email, random, EmailUtil.EMAIL_TIME);
            redisUtil.set(email, 1, EmailUtil.SIGN_TIME);
            return messageResult;
        }
        return new MessageResult(false, "没有这个邮箱");
    }

    /**
     * 通过验证邮箱更改密码
     *
     * @param email    用户的邮箱
     * @param newPwd 新的密码
     * @param code 验证码
     * @return
     */
    @Override
    public MessageResult updatePasswordByEmail(String email, String newPwd, String code) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",email);
        User user = getOne(wrapper);
        if (user == null){
            return new MessageResult(false, "该邮箱没有注册");
        }
        if (checkEmailCode("upd_"+user.getEmail(),code)) {
            MessageResult messageResult = updatePwd(user, newPwd);
            return messageResult;
        }else {
            return new MessageResult(false,"验证码错误");
        }

    }

    /**
     * 通过旧密码修改密码
     *
     * @param uid    用户的id
     * @param oldPwd 旧的密码
     * @param newPwd 新的密码
     * @return
     */
    @Override
    public MessageResult updatePassword(int uid, String oldPwd, String newPwd) {
        User user = userMapper.selectById(uid);
        if (MD5Util.convertMD5(user.getPassword()).equals(MD5Util.string2MD5(oldPwd))){
            MessageResult messageResult = updatePwd(user, newPwd);
            return messageResult;

        }else {
            return new MessageResult(false, "原密码错误");
        }
    }

    /**
     * 修改用户的用户名
     *
     * @param uid  用户的id
     * @param name 新的用户名
     * @return
     */
    @Override
    public MessageResult updateUsername(int uid, String name) {
        User user = userMapper.selectById(uid);
        user.setUsername(name);
        userMapper.updateById(user);
        return new MessageResult(true, "修改成功");
    }

    /**
     * 修改用户的头像链接
     *
     * @param uid 用户的id
     * @param url 用户的头像链接
     * @return
     */
    @Override
    public MessageResult updatePicUrl(int uid, String url) {
        User user = userMapper.selectById(uid);
        user.setPrictureUrl(url);
        userMapper.updateById(user);
        return new MessageResult(true, "修改成功");
    }

    /**
     * 修改用户的邮箱
     *
     * @param uid   用户的id
     * @param email 用户的邮箱
     * @return
     */
    @Override
    public MessageResult updateEmail(int uid, String email, String code) {
        User user = userMapper.selectById(uid);
        if (checkEmailCode("upd_"+user.getEmail(),code)) {
            user.setEmail(email);
            userMapper.updateById(user);
            return new MessageResult(true, "修改成功");
        }else {
            return new MessageResult(false, "验证码错误");
        }
    }

    /**
     * 通过账号获取用户信息
     *
     * @param accountNumber
     * @return
     */
    @Override
    public User getUser(String accountNumber) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account_number", accountNumber);
        User user = getOne(wrapper);
        if (user == null){
            return null;
        }
        return user;
    }

    /**
     * 管理员直接修改密码
     *
     * @param uid    用户id
     * @param newPwd 新的密码
     * @return
     */
    @Override
    public MessageResult updatePassword(int uid, String newPwd) {
        User user = userMapper.selectById(uid);
        MessageResult messageResult = updatePwd(user, newPwd);
        return messageResult;
    }

    /**
     * 分页获取用户的权限
     *
     * @param row
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<UserAuthentication> getUserAuthentication(int row, int pageSize) {
        // 1. 查找到该页的所有用户
        Page<User> userPage = new  Page<User>();
        userPage.setSize(pageSize);
        userPage.setPages(row);
        IPage<User> userIPage = userMapper.selectPage(userPage, null);
        // 2. 设置分页返回的信息
        PageResult<UserAuthentication> pageResult = new PageResult<>();
        pageResult.setRow(userIPage.getCurrent());
        pageResult.setTatolSize(userIPage.getTotal());
        pageResult.setPageSize(userIPage.getSize());
        // 分装用户和权限
        List<UserAuthentication> userAuthentications = new LinkedList<>();
        for (User user : userIPage.getRecords()){
            QueryWrapper<Authentication> wrapper = new QueryWrapper<>();
            wrapper.eq("uid",user.getUid());
            List<Authentication> authentications = authenticationMapper.selectList(wrapper);
            List<String> roles = new LinkedList<>();
            for(Authentication authentication : authentications){
                roles.add(authentication.getRole());
            }
            UserAuthentication userAuthentication = new UserAuthentication(user.getUid(),user.getAccountNumbser(),user.getEmail(),roles);
            userAuthentications.add(userAuthentication);
        }
        pageResult.setPageContext(userAuthentications);
        return pageResult;
    }

    /**
     * 判断是否有这个用户，用于给其他模块验证是否存在用户
     *
     * @param uid 用户的id
     * @return
     */
    @Override
    public boolean isUser(int uid) {
        User user = getById(uid);
        if (user == null){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取实验室所有成员
     *
     * @return
     */
    @Override
    public List<User> getCcwMenber() {
        return userMapper.getCcwMenber();
    }

    /**
     * 获取正在实验室的成员
     *
     * @return
     */
    @Override
    public List<User> getCcwIngMenber() {
        return userMapper.getCcwIngMenber();
    }


    /**
     * 查询邮箱是否注册过
     * @param email 邮箱
     * @return
     */
    private boolean checkEmail(String email){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email",email);
        Integer count = userMapper.selectCount(wrapper);
        if (count == 0){
            return true;
        }else {
            return false;
        }
    }


    /**
     * 检查用户账户是否已经被注册
     * @param accountNumber
     * @return
     */
    private boolean checkAccountNUmber(String accountNumber){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account_number", accountNumber);
        Integer count = userMapper.selectCount(wrapper);
        if (count == 0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 检查用户信息是否正确
     * @param user
     * @return
     */
    private boolean checkUser(User user){
        // 如果用户名为空
        if (user.getAccountNumbser() == null || "".equals(user.getAccountNumbser())){
            return false;
        }
        if (user.getEmail() == null || "".equals(user.getEmail())){
            return false;
        }
        if (user.getPassword() == null || "".equals(user.getPassword())){
            return false;
        }
        if (user.getUsername() == null || "".equals(user.getUsername())){
            return false;
        }
        return true;
    }

    /**
     * 修改密码
     * @param user
     * @param newPwd
     * @return
     */
    @Transactional
    MessageResult updatePwd(User user, String newPwd){
        try {
            String decodePwd = MD5Util.convertMD5(MD5Util.string2MD5(newPwd));
            user.setPassword(decodePwd);
            userMapper.updateById(user);
            user = userMapper.selectById(user.getUid());
            if (!MD5Util.string2MD5(newPwd).equals(MD5Util.convertMD5(user.getPassword()))) {
                throw new Exception("密码修改失败");
            } else {
                return new MessageResult(true, "修改成功");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new MessageResult(false, e.getMessage());
        }
    }
}
