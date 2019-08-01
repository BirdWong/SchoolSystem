package cn.jsuacm.ccw.service.blog;

import cn.jsuacm.ccw.pojo.blog.Category;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName CategoryService
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/21 17:34
 */
public interface CategoryService extends IService<Category>{

    /**
     * 获取所有的分类信息，将以map的形式返回，一级分类为键， 二级分类为值
     * @return
     */
    public Map<Category, List<Category>> getAllCategory();


    /**
     * 添加一级分类
     * @param cname 分类名
     * @return
     */
    public MessageResult saveParent(String cname);


    /**
     * 添加二级分类
     * @param category
     * @return
     */
    public MessageResult saveChildren(Category category);


    /**
     * 删除分类
     * @param cid
     * @return
     */
    public MessageResult deleteCategory(int cid);


    /**
     * 修改分类
     * @param category
     * @return
     */
    public MessageResult updateCategory(Category category);


    /**
     * 查找所有的一级分类
     * @return
     */
    public List<Category> findFirstCategories();


    /**
     * 通过一个一级分类id，查询所属的二级分类
     * @param cid
     * @return
     */
    public List<Category> findSecondCategories(int cid);
}
