package cn.jsuacm.ccw.controller;

import cn.jsuacm.ccw.pojo.Category;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName CategoryController
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/21 22:07
 */
@RestController
@RequestMapping(value = "category")
@Api(value = "category",description = "分类操作")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 得到所有的分类
     * @return
     */
    @GetMapping(value = "getAllCategory")
    @ApiOperation(value = "获取所有分类",notes = "获取所有的分类集合，以一级分类为键，二级分类集合为值", httpMethod = "get")
    public Map<Category, List<Category>> getAllCategory(){
        Map<Category, List<Category>> allCategory = categoryService.getAllCategory();
        return allCategory;
    }

    /**
     * 添加一个一级分类
     * @param cname
     * @return
     */
    @PostMapping(value = "addFirstCategory")
    @ApiOperation(value = "添加一个一级分类", notes = "添加一个一级分类，如果已经有这个一级分类了则添加失败", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cname", required = true, value = "新加一级分类的名称", dataType = "string" , paramType = "query")
    })
    public MessageResult addFirstCategory(String cname){
        MessageResult messageResult = categoryService.saveParent(cname);
        return messageResult;
    }


    /**
     * 添加一个二级分类
     * @param category
     * @return
     */
    @PostMapping(value = "addSencondCategory")
    @ApiOperation(value = "添加一个二级分类" , notes = "添加二级分类如果该二级分类下的一级分类已经有这个分类了，则添加失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", required = true, value = "二级分类的父分类id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "cname", required = true, value = "二级分类的名称", dataType = "string", paramType = "query")
    })
    public MessageResult addSencondCategory(@RequestBody Category category){
        MessageResult messageResult = categoryService.saveChildren(category);
        return messageResult;
    }


    /**
     * 删除一个分类
     * @param cid
     * @return
     */
    @GetMapping(value = "deleteCategory/{cid}")
    @ApiOperation(value = "删除一个一级分类", notes = "删除一个一级分类， 同时会删除其附属的二级分类以及二级分类相关的文章信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value = "一级分类的id", dataType = "int", paramType = "query")
    })
    public MessageResult deleteCategory(@PathVariable(value = "cid") int cid){
        MessageResult messageResult = categoryService.deleteCategory(cid);
        return messageResult;
    }


    /**
     * 更新一个分类
     * @param category
     * @return
     */
    @PostMapping(value = "updateCategory")
    @ApiOperation(value = "更新一个分类" , notes = "更新一个分类信息", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value = "分类的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "parentId", required = true, value = "父级分类id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "cname", required = true, value = "分类的名称", dataType = "string", paramType = "query")
    })
    public MessageResult updateCategory(@RequestBody Category category){
        MessageResult messageResult = categoryService.updateCategory(category);
        return messageResult;
    }


    /**
     * 返回所有一级分类信息
     * @return
     */
    @GetMapping("getFirstCategory")
    @ApiOperation(value = "查找所有的一级分类信息")
    public List<Category> findFirstCategories(){
        return categoryService.findFirstCategories();
    }


    /**
     * 查找一个一级分类下的所有二级分类
     * @param cid
     * @return
     */
    @GetMapping("getSecondByCid/{cid}")
    @ApiOperation(value = "查找一个一级分类下的二级分类", notes = "通过一级分类的id， 查找属于这个一级分类的二级分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value = "一级分类id", dataType = "int", paramType = "path")
    })
    public List<Category> findSecondCategories(@PathVariable(value = "cid") int cid){
        List<Category> categories = categoryService.findSecondCategories(cid);
        return categories;
    }
}
