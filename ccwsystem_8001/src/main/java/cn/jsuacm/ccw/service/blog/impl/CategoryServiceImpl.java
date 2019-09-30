package cn.jsuacm.ccw.service.blog.impl;

import cn.jsuacm.ccw.mapper.blog.CategoryMapper;
import cn.jsuacm.ccw.pojo.blog.Category;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.blog.ArticleInfomationService;
import cn.jsuacm.ccw.service.blog.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CategoryServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/21 17:35
 */

@Service

public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    /**
     * 分类的mapper操作类
     */
    @Autowired
    private CategoryMapper categoryMapper;




    @Autowired
    private ArticleInfomationService articleInfomationService;


    /**
     * 获取所有的分类信息，将以map的形式返回，一级分类为键， 二级分类为值
     *
     * @return
     */
    @Override

    public Map<Category, List<Category>> getAllCategory() {
        // 获取所有的一级分类
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", 0);
        List<Category> categories = categoryMapper.selectList(wrapper);
        Map<Category, List<Category>> categoryListMap = new HashMap<>();
        // 获取所有一级分类的二级分类
        for (Category category : categories){
            wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", category.getCid());
            List<Category> categoryList = categoryMapper.selectList(wrapper);
            categoryListMap.put(category, categoryList);
        }

        return categoryListMap;
    }

    /**
     * 添加一级分类
     *
     * @param cname 分类名
     * @return 返回分类的id
     */
    @Override
    public MessageResult saveParent(String cname) {
        // 1. 确认一级分类中没有这个分类
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("cname",cname).eq("parent_id", 0);
        Integer count = categoryMapper.selectCount(wrapper);
        if (count != 0){
            return new MessageResult(false, "已经有这个一级分类");
        }
        Category category = new Category();
        category.setCname(cname);
        category.setParentId(0);
        save(category);
        return new MessageResult(true, category.getCid()+"");
    }

    /**
     * 保存二级分类
     *
     * @param category
     * @return
     */
    @Override
    public MessageResult saveChildren(Category category) {

        // 验证是否有这个一级分类
        if(category.getParentId() == 0 || getById(category.getParentId()) == null){
            return new MessageResult(false, "没有这个一级分类");
        }else{
            // 查看这个一级分类下是否已经有这个二级分类
            QueryWrapper<Category> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id",category.getParentId()).eq("cname", category.getCname());
            if(getOne(wrapper) != null){
                return new MessageResult(false, "已经有这个二级分类");
            }else {
                save(category);
                return new MessageResult(true, category.getCid()+"");
            }
        }
    }

    /**
     * 删除分类
     * @param cid
     * @return
     */
    @Override
    public MessageResult deleteCategory(int cid) {
        // 1. 检测是否有这个分类
        Category category = getById(cid);
        if (category == null){
            return new MessageResult(false, "没有这个分类");
        }
        // 2. 检测是否有子分类未删除
        if (category.getParentId() == 0){
            QueryWrapper<Category> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", cid);
            List<Category> categories = categoryMapper.selectList(wrapper);
            // 删除子分类
            if (categories.size() != 0){
                for (Category tmp : categories){
                    articleInfomationService.deleteAllBySencondCid(tmp.getCid());
                    categoryMapper.deleteById(tmp.getCid());
                }
            }

        }else {
            // 删除对应分类的文章
            articleInfomationService.deleteAllBySencondCid(cid);
        }
        // 4. 删除分类
        categoryMapper.deleteById(cid);

        return new MessageResult(true, "删除成功");
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     */
    @Override
    public MessageResult updateCategory(Category category) {
        // 1. 判断是否是一级分类
        Category tmpCategory = getById(category.getCid());
        if (tmpCategory.getParentId() == 0 && category.getParentId() != 0){
            return new MessageResult(false, "一级分类不允许有父分类");
        }
        // 2. 判断如果时二级分类的话，他设置的父分类是否存在， 是否是一级分类
        // 如果是二级分类
        if (category.getParentId() != 0){
            tmpCategory = getById(category.getParentId());
            // 如果父分类存在
            if (tmpCategory != null){
                // 如果父分类是一级分类
                if (tmpCategory.getParentId() == 0){
                    updateById(category);
                    return new MessageResult(true, "修改成功");
                }else {
                    return new MessageResult(false, "设置的父分类不是一级分类");
                }
            }else {
                return new MessageResult(false, "没有这个父分类");
            }
        }else {
            updateById(category);
            return new MessageResult(true, "修改成功");
        }
    }

    /**
     * 查找所有的一级分类
     * @return
     */
    @Override

    public List<Category> findFirstCategories() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        return categoryMapper.selectList(queryWrapper);
    }

    /**
     * 通过一个一级分类id获取其下面的子分类
     *
     * @param cid 一级分类id
     * @return
     */
    @Override

    public List<Category> findSecondCategories(int cid) {

        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cid", cid).eq("parent_id", 0);
        Integer count = categoryMapper.selectCount(queryWrapper);
        if (count == 0){
            return null;
        }
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", cid);
        List<Category> categories = categoryMapper.selectList(wrapper);
        return categories;
    }
}
