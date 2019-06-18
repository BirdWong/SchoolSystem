package cn.jsuacm.gateway.service;

import cn.jsuacm.gateway.pojo.Authentication;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @ClassName AuthenticationService
 * @Description 权限服务层接口
 * @Author h4795
 * @Date 2019/06/17 21:40
 */
public interface AuthenticationService extends IService<Authentication>{

    /**
     * 工作室权限
     */
    public static String MENBER = "ROLE_MENBER";


    /**
     * 模块管理员权限
     */
    public static String ADMIN = "ROLE_ADMIN";

    /**
     * 老师权限
     */
    public static String TEACHER = "ROLE_TEACHER";


    /**
     * 超级管理员权限
     */
    public static String ADMINISTRATOR = "ROLE_ADMINISTRATOR";


    /**
     * 添加用户实验室成员权限
     * @param uid 被添加权限的id
     * @return
     */
    public boolean addMemBerAuthentication(int uid);


    /**
     * 添加管理员权限
     * @param uid 被添加权限的id
     * @return
     */
    public boolean addAdminAuthentication(int uid);


    /**
     * 添加老师权限
     * @param uid 被添加权限的id
     * @return
     */
    public boolean addTeacherAuthentication(int uid);


    /**
     * 添加超级管理员权限
     * @param uid 被添加权限的id
     * @return
     */
    public boolean addAdministratorAuthentication(int uid);


    /**
     * 删除用户实验室成员权限
     * @param uid 被删除权限的id
     * @return
     */
    public boolean deleteMemBerAuthentication(int uid);


    /**
     * 删除管理员权限
     * @param uid 被删除权限的id
     * @return
     */
    public boolean deleteAdminAuthentication(int uid);


    /**
     * 删除老师权限
     * @param uid 被删除权限的id
     * @return
     */
    public boolean deleteTeacherAuthentication(int uid);


    /**
     * 删除超级管理员权限
     * @param uid 被删除权限的id
     * @return
     */
    public boolean deleteAdministratorAuthentication(int uid);









    /**
     * 通过用户账号获取用户的所有权限
     * @param uid
     * @return
     */
    public List<String> getGrantedAuthorities(int uid);

}
