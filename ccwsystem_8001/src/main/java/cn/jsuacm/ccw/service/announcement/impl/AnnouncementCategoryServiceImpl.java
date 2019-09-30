package cn.jsuacm.ccw.service.announcement.impl;

import cn.jsuacm.ccw.mapper.announcement.AnnouncementCategoryMapper;
import cn.jsuacm.ccw.pojo.announcement.AnnouncementCategory;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.announcement.AnnouncementCategoryService;
import cn.jsuacm.ccw.service.announcement.AnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CategoryServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/21 22:18
 */
@Service

public class AnnouncementCategoryServiceImpl extends ServiceImpl<AnnouncementCategoryMapper, AnnouncementCategory> implements AnnouncementCategoryService {

    @Autowired
    private AnnouncementCategoryMapper announcementCategoryMapper;

    @Autowired
    private AnnouncementService announcementService;


    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate template;

    private String url = "http://JSUCCW-ZUUL-GATEWAY/authentication/getAuthenticationByUid/";

    /**
     * 添加公告的分类，分类中确认这个用户的权限高于或者等于分类中设置的权限
     *
     * @param uid      用户的id
     * @param announcementCategory 分类
     * @return
     */
    @Override
    public MessageResult add(int uid, AnnouncementCategory announcementCategory) {
        // 验证是否有自己的这个权限, 如果查阅权限是最低的可以直接添加
        if (compareToRole(uid, announcementCategory.getRole())){
            // 验证是否有这个分类名
            if (announcementCategoryMapper.hasCategory(announcementCategory.getName()) == 0){
                Integer count = announcementCategoryMapper.selectCount(null);
                if (count>=10){
                    return new MessageResult(false, "分类数量已经达到10个，请删除后在添加");
                }
                int insert = announcementCategoryMapper.insert(announcementCategory);
                // 确认插入是否成功
                if (insert != 0){
                    // 返回内容中把插入得到的id返回
                    return new MessageResult(true, ""+announcementCategory.getId());
                }
            }else {
                return new MessageResult(false, "已经存在这个公告分类");
            }
        }
        return new MessageResult(false, "限定条件高于本身拥有权限");
    }

    /**
     * 通过一个id获取一个分类的所有信息
     *
     * @param id 分类的id
     * @return
     */
    @Override

    public AnnouncementCategory getForId(int id) {
        if (id < 1){
            return null;
        }
        return getById(id);
    }

    /**
     * 修改分类的信息
     * @param uid 用户的id
     * @param announcementCategory 分类的信息
     * @return
     */
    @Override

    public MessageResult update(int uid, AnnouncementCategory announcementCategory) {

        // 确认这个用户是否有这个权限
        if (compareToRole(uid, announcementCategory.getRole())) {

            /*
             * 确认这个分类之前的查看权限级别，以防低权限妄图修改高级别权限查看信息
             * 1. 获取这个分类本来的信息
             * 2. 比对分类的信息权限
             */
            AnnouncementCategory category = getById(uid);
            if (category == null){
                return  new MessageResult(false, "需要修改的分类不存在");
            }
            // 原来的权限低于用户拥有的权限
            if (compareToRole(uid, category.getRole())){
                // 验证是否有这个分类名
                if (announcementCategoryMapper.hasCategory(announcementCategory.getName()) == 0) {
                    boolean isOk = updateById(announcementCategory);
                    if (isOk){
                        return new MessageResult(true, "修改成功");
                    }else {
                        return new MessageResult(false, "修改失败");
                    }
                } else {
                    return new MessageResult(false, "已经存在这个分类名");
                }
            }else {
                return new MessageResult(false, "分类本设计的权限高举用户拥有权限");
            }
        }
        return new MessageResult(false, "用户权限无法修改此查阅权限");
    }

    /**
     * 通过分类的id，删除一个分类
     *
     * @param uid 用户的id
     * @param id  分类的id
     * @return
     */
    @Override

    public MessageResult delete(int uid, int id) {

        // 确认这个分类是否真的存在
        AnnouncementCategory category = getById(id);
        if (category == null){
            return new MessageResult(false, "没有这个分类");
        }
        // 确认这个用户的有这个权限删除这个分类
        if (compareToRole(uid, category.getRole())){

            // 确认这个分类下是否还有公告信息
            if (!announcementService.hasOfCid(id)){
                boolean isOk = removeById(id);
                // 确认是否删除成功
                if (isOk){
                    return new MessageResult(true, "删除成功");
                }else {
                    return  new MessageResult(false, "删除失败");
                }
            }else {
                return new MessageResult(false, "还有公告信息没有清除");
            }
        }else {
            return new MessageResult(false, "分类权限高于此用户权限");
        }


    }

    /**
     * 获得ALL权限的公告分类
     *
     * @return
     */
    @Override

    public List<AnnouncementCategory> getOfAll() {
        QueryWrapper<AnnouncementCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", AnnouncementCategory.Role.ALL);
        List<AnnouncementCategory> announcementCategories = announcementCategoryMapper.selectList(queryWrapper);
        if (announcementCategories == null){
            announcementCategories = new ArrayList<>();
        }
        return announcementCategories;
    }

    /**
     * 获得ING权限能够看到的公告分类
     * @param uid  用户的id
     * @return
     */
    @Override

    public List<AnnouncementCategory> getOfIng(int uid) {
        if (compareToRole(uid, AnnouncementCategory.Role.ROLE_ING)) {
            QueryWrapper<AnnouncementCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role", AnnouncementCategory.Role.ALL).or().eq("role", AnnouncementCategory.Role.ROLE_ING);
            List<AnnouncementCategory> announcementCategories = announcementCategoryMapper.selectList(queryWrapper);
            return announcementCategories;
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * 获得ADMIN权限能够看到的公告分类
     * @param uid  用户的id
     * @return
     */
    @Override

    public List<AnnouncementCategory> getOfAdmin(int uid) {
        if (compareToRole(uid, AnnouncementCategory.Role.ROLE_ADMIN)) {
            QueryWrapper<AnnouncementCategory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role", AnnouncementCategory.Role.ALL).or().eq("role", AnnouncementCategory.Role.ROLE_ING).or().eq("role", AnnouncementCategory.Role.ROLE_ADMIN);
            List<AnnouncementCategory> announcementCategories = announcementCategoryMapper.selectList(queryWrapper);
            if (announcementCategories == null){
                announcementCategories = new ArrayList<>();
            }
            return announcementCategories;
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * 获得Administrator权限能够看到的公告分类
     * @param uid  用户的id
     * @return
     */
    @Override

    public List<AnnouncementCategory> getOfAdministrator(int uid) {
        if (compareToRole(uid, AnnouncementCategory.Role.ROLE_ADMINISTRATOR)) {
            QueryWrapper<AnnouncementCategory> queryWrapper = new QueryWrapper<>();
            List<AnnouncementCategory> announcementCategories = announcementCategoryMapper.selectList(queryWrapper);
            if (announcementCategories == null){
                announcementCategories = new ArrayList<>();
            }
            return announcementCategories;
        }else {
            return new ArrayList<>();
        }
    }


    /**
     * 获取一个用户id的所有权限
     * @param uid
     * @return
     */
    private List<String> getRole(int uid){
        // 添加请求头中的校验
        HttpHeaders handler = new HttpHeaders();
        handler.add("Uid", String.valueOf(uid));
        HttpEntity<String> entity = new HttpEntity<>(null, handler);
        ResponseEntity<List> responseEntity = template.exchange(url + uid, HttpMethod.GET,entity,List.class);
        List body = responseEntity.getBody();
        ArrayList<String> roleList = new ArrayList<>();
        // 如果请求中没有信息则返回空列表
        if (body == null || body.size() == 0){
            return roleList;
        }
        // 将请求头中的内容转换成map，并且提取其中的权限数据
        for (Object obj : body){

            HashMap<String, Object> values = (HashMap<String, Object>) obj;
            Object role = values.get("role");
            if (role != null){
                roleList.add(String.valueOf(role));
            }
        }

        return roleList;
    }


    /**
     * 比较用户权限信息
     * @param uid 用户的id
     * @param role 查看的权限大小
     * @return
     */
    @Override
    public boolean compareToRole(int uid, AnnouncementCategory.Role role){

        // 如果权限的大小是最低级权限不用通过校验
        if (role.compareTo(AnnouncementCategory.Role.ALL) == 0){
            return true;
        }

        if (uid < 1){
            return  false;
        }
        // 获取这个用户的所有权限
        List<String> roleList = getRole(uid);
        // 遍历权限确认用户的权限高于等于设定查阅的权限
        for (String r : roleList){
            // 如果没有这个权限选项，直接认定为最低权限
            if (!AnnouncementCategory.Role.hasRole(r)){
                r = "ALL";
            }
            // 如果拥有的权限大小设定的大小，返回true
            if(AnnouncementCategory.Role.valueOf(r).compareTo(role) >= 0){
                return true;
            }
        }

        // 遍历完成后还没有找到符合的权限就返回false
        return false;
    }
}
