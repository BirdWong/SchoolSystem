package cn.jsuacm.ccw.controller;

import cn.jsuacm.ccw.pojo.Article;
import cn.jsuacm.ccw.pojo.ArticleInfomation;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.service.ArticleInfomationService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName ArticleInfomationController
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/26 16:18
 */
@RestController
@RequestMapping("articleInfomation")
@Api(value = "articleInfomation", description = "调用这个删除的接口时注意！ 接口内会将剩下的部分全部执行完毕（仅仅针对博客部分的接下来的操作），示例1——删除一个用户1：</br>1、调用删除角色接口</br> 2、调用删除角色其他信息的接口<br> 3、 开始删除该用户的博客部分信息，调用'admin/deleteAllByUid/{uid}'接口， 接口内会删除文章信息表中这个用户的文章信息，然后自动删除用户创建的所有标签，最后自动删除用户写的所有用户属性的文章，也就是说删除角色的博客信息部分只要一个调用接口就可以了</br> 示例二： 删除一个二级分类 </br>1. 调用删除二级分类接口\n 2. 调用'admin/deleteAllByUid/{cid}'接口， 会自动删除文章信息表中与这个分类相关的信息， 然后自动删除包含这个分类的用户的文章")
public class ArticleInfomationController {

    @Autowired
    private ArticleInfomationService articleInfomationService;


    /**
     * 添加一个文章信息
     * @param req
     * @param articleInfomation
     * @return
     */
    @PostMapping(value = "add")
    @ApiOperation(value = "添加一个用户的文章信息", notes = "用户写完文章保存时，首先将文章保存到文章列表，然后从返回值获取文章的id， 如果有新的标签加入也是首先保存标签。然后回去标签的返回值或者重新获取标签列表，然后将信息填充完整", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "cid", required = true, value = "二级分类的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "lids", required = false, value = "文章标签对象", dataType = "ArticleLabel", paramType = "query")
    })
    public MessageResult add(HttpServletRequest req, @RequestBody ArticleInfomation articleInfomation){
        boolean isUser = CheckUserUtil.isUser(req, articleInfomation.getUid());
        if (isUser){
            return articleInfomationService.add(articleInfomation);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


    /**
     * 通过文章信息id删除文章
     * @param uid 用户id
     * @param id 文章信息id
     * @param req
     * @return
     */
    @ApiOperation(value = "通过文章信息id删除一个文章信息", notes = "此删除仅仅删除信息，其他的不会有任何操作", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "id", required = true, value = "文章信息的id", dataType = "int", paramType = "path")
    })
    @GetMapping("deleteById/{uid}/{id}")
    public MessageResult deleteById(@PathVariable(value = "uid") int uid, @PathVariable(value = "id") int id, HttpServletRequest req){
        boolean isUser = CheckUserUtil.isUser(req, uid);
        if (isUser){
            return articleInfomationService.deleteById(uid, id);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


    /**
     * 用户通过文章id删除一个文章信息
     * @param req 带有token信息的请求对象
     * @param uid 用户id
     * @param aid 文章id
     * @return
     */
    @GetMapping("deleteByAid/{uid}/{aid}")
    @ApiOperation(value = "通过文章的id删除一个文章信息", notes = "通过用户的id获取文章的信息列表， 通过信息列表中的aid删除一个文章信息, 返回的uid和token必须用一个人的， 会进行token二次验证", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteByAid(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "aid")int aid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleInfomationService.deleteByAid(aid);
        }else {
            return new MessageResult(false, "uid与token中的不匹配");
        }
    }


    /**
     * 管理员通过文章的id删除一篇文章信息
     * @param aid
     * @return
     */
    @GetMapping("admin/deleteByAid/{aid}")
    @ApiOperation(value = "管理员通过文章的aid删除一篇文章信息", notes = "通过获取到的文章id删除一篇文章信息，操作者必须有管理员权限", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", value = "文章id", dataType = "int", paramType = "path")
    })
    public MessageResult adminDeleteByAid(@PathVariable(value = "aid") int aid){
        return articleInfomationService.deleteByAid(aid);
    }


    /**
     * 用户通过一个标签id删除标签下的文章信息
     * @param req 包含token的请求对象
     * @param uid 用户id
     * @param lid 标签id
     * @return
     */
    @GetMapping(value = "deleteAllByLid/{uid}/{lid}")
    @ApiOperation(value = "用户通过标签id删除标签下的所有文章", notes = "登陆过后的用户获取自己创建的标签信息， 通过标签的id删除属于这个标签的所有文章信息以及文章", httpMethod = "path")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "lid", value = "标签的id", required = true, dataType = "int", paramType = "path")
    })
    public MessageResult deleteAllByLid(HttpServletRequest req, @PathVariable(value = "uid")int uid, @PathVariable("lid") int lid){
        if (CheckUserUtil.isUser(req, uid)) {
            return articleInfomationService.deleteAllByLid(uid, lid);
        }else {
            return new MessageResult(false, "token用户信息与参数不同");
        }
    }







    /**
     * 管理员通过一个标签id删除标签下的文章信息
     * @param uid 用户id
     * @param lid 标签id
     * @return
     */
    @GetMapping(value = "admin/deleteAllByLid/{lid}")
    @ApiOperation(value = "管理员通过标签id删除标签下的所有文章", notes = "管理员获取用户的文章或者标签信息， 通过标签的id删除属于这个标签的所有文章信息以及文章", httpMethod = "path")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lid", value = "标签的id", required = true, dataType = "int", paramType = "path")
    })
    public MessageResult adminDeleteAllByLid(@PathVariable(value = "uid")int uid, @PathVariable("lid") int lid){
            return articleInfomationService.deleteAllByLid(uid, lid);
    }


    /**
     * 管理员通过二级分类id删除一个分类下的所有文章信息以及文章
     * @param cid 二级分类id
     * @return
     */
    @GetMapping("admin/deleteAllBySencondCid/{cid}")
    @ApiOperation(value = "通过二级分类id删除文章信息以及文章", notes = "通过二级分类的id删除这个二级分类在博客中的所有信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value="二级分类id", dataType = "int", paramType = "path")
    })
    public MessageResult adminDeleteAllBySencondCid(int cid){
        return articleInfomationService.deleteAllBySencondCid(cid);
    }


    /**
     * 通过用户id删除所有文章、标签等信息
     * **********必须要管理员或者超级管理员权限************
     * @param uid 用户id
     * @return
     */
    @GetMapping("admin/deleteAllByUid/{uid}")
    @ApiOperation(value = "删除用户在博客模块中的所有信息", notes = "会删除用户在博客信息中的记录， 并且删除用户创建的标签，以及用户的所有用户属性文章， 公告通知文章会转移到超级管理员账号下", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteAllByUid(@PathVariable(value = "uid") int uid){
        return articleInfomationService.deleteAllByUid(uid);
    }


    /**
     * 通过uid获取这个用户的所有公开信息文章
     * @param uid 用户id
     * @return
     */
    @GetMapping("getPublicByUid/{uid}")
    @ApiOperation(value = "获取这个用户的公开文章信息", notes = "通过用户的uid信息获取这个用户的公开文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getPublicByUid(@PathVariable(value = "uid") int uid){
        return articleInfomationService.getByUid(uid, Article.PUBLIC_ARTICLE);
    }


    /**
     * 用户通过uid查询属于这个uid的私有文章
     * @param req 包含token的请求对象
     * @param uid 用户id
     * @return
     */
    @GetMapping("getPrivateByUid/{uid}")
    @ApiOperation(value = "获取用户自己的私有文章信息列表", notes = "用户获取自己的私人文章列表，必去已经登录， 会使用token二级校验", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getPrivateByUid(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleInfomationService.getByUid(uid, Article.PRIVETA_ARTICLE);
        }else {
            return null;
        }
    }


    /**
     * 用户通过uid查询属于这个uid的草稿文章
     * @param req 包含token的请求对象
     * @param uid 用户id
     * @return
     */
    @GetMapping("getDraftByUid/{uid}")
    @ApiOperation(value = "获取用户自己的草稿文章信息列表", notes = "用户的获取自己的草稿文章列表， 必须已经登录， 会token二次校验", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getDraftByUid(HttpServletRequest req, @PathVariable(value = "uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleInfomationService.getByUid(uid, Article.DRAFT_ARTICLE);
        }else {
            return null;
        }
    }


    /**
     * 管理员通过用户的id查询这个用户的私有文章信息
     * @param uid 用户的id
     * @return
     */
    @GetMapping("admin/getPrivateByUid/{uid}")
    @ApiOperation(value = "管理员获取一个用户的私人文章列表",notes = "管理员获取一个用户的私人文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> adminGetPrivateByUid(@PathVariable(value = "uid") int uid){
        return articleInfomationService.getByUid(uid, Article.PRIVETA_ARTICLE);
    }



    /**
     * 管理员通过用户的id查询这个用户的草稿文章信息
     * @param uid 用户的id
     * @return
     */
    @ApiOperation(value = "管理员获取一个用户的草稿文章列表",notes = "管理员获取一个用户的草稿文章列表", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    @GetMapping("admin/getDraftByUid/{uid}")
    public List<ArticleInfomation> adminGetDraftByUid(@PathVariable(value = "uid") int uid){
        return articleInfomationService.getByUid(uid, Article.DRAFT_ARTICLE);
    }



    /**
     * 通过文章的id获取一个公开文章信息
     * @param aid 文章id
     * @return
     */
    @GetMapping("getPublicByAid/{aid}")
    @ApiOperation(value = "通过文章id获取一篇公开文章的文章信息", notes = "通过文章id获取一篇公开文章的文章信息", httpMethod = "get")
    public ArticleInfomation getPublicByAid(@PathVariable(value = "aid") int aid){
        return articleInfomationService.getByAid(aid, Article.PUBLIC_ARTICLE);
    }



    /**
     * 用户通过文章的id获取一个私有文章信息
     * @param aid 文章id
     * @param uid 用户id
     * @return
     */
    @GetMapping("getPrivateByAid/{uid}/{aid}")
    @ApiOperation(value = "用户通过文章id获取一个自己的私人文章信息", notes = "已经登录的用户通过文章的id获取这个私人文章的详细信息， 会toekn二次校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "aid", value = "文章的id", required = true, dataType = "int", paramType = "path")
    })
    public ArticleInfomation getPrivateByAid(HttpServletRequest req,@PathVariable(value = "uid") int uid, @PathVariable(value = "aid") int aid){
        if (CheckUserUtil.isUser(req, uid)) {
            return articleInfomationService.getByAid(aid, Article.PRIVETA_ARTICLE);
        }else {
            return null;
        }
    }



    /**
     * 用户通过文章的id获取一个草稿文章信息
     * @param aid 文章id
     * @param uid 用户id
     * @return
     */
    @GetMapping("getDraftByAid/{uid}/{aid}")
    @ApiOperation(value = "用户通过文章id获取一个自己的草稿文章信息", notes = "已经登录的用户通过文章的id获取这个草稿文章的详细信息，会token二次校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "aid", value = "文章的id", required = true, dataType = "int", paramType = "path")
    })
    public ArticleInfomation getDraftByAid(HttpServletRequest req,@PathVariable(value = "uid") int uid, @PathVariable(value = "aid") int aid){
        if (CheckUserUtil.isUser(req, uid)) {
            return articleInfomationService.getByAid(aid, Article.DRAFT_ARTICLE);
        }else {
            return null;
        }
    }


    /**
     * 管理员获取一篇私有文章信息
     * @param aid 文章id
     * @return
     */
    @GetMapping("admin/getPrivateByAid/{aid}")
    @ApiOperation(value = "管理员通过文章的id获取的私人文章信息",notes = "管理员通过文章的id获取的私人文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "path")
    })
    public ArticleInfomation adminGetPrivateByAid(@PathVariable(value = "aid") int aid){
        return articleInfomationService.getByAid(aid, Article.PRIVETA_ARTICLE);
    }




    /**
     * 用户通过文章的id获取一个草稿文章信息
     * @param aid 文章id
     * @return
     */
    @GetMapping("admin/getDraftByAid/{aid}")
    @ApiOperation(value = "管理员通过文章的id获取的草稿文章信息",notes = "管理员通过文章的id获取的草稿文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "path")
    })
    public ArticleInfomation adminGetDraftByAid(@PathVariable(value = "aid") int aid){
        return articleInfomationService.getByAid(aid, Article.DRAFT_ARTICLE);
    }



    /**
     * 通过标签的id获取公开文章信息
     * @param lid 标签id
     * @return
     */
    @GetMapping("getPublicByLid/{lid}")
    @ApiOperation(value = "通过标签信息公开文章信息", notes = "通过标签的lid信息获取这个标签的公开文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lid", required = true, value = "标签的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getPublicByLid(@PathVariable(value = "lid") int lid){
        return articleInfomationService.getByLid(lid, Article.PUBLIC_ARTICLE);
    }


    /**
     * 用户通过标签id获取私有的文章信息
     * @param req 包含token的对象
     * @param uid 用户id
     * @param lid 标签id
     * @return
     */
    @GetMapping("getPrivateByLid/{uid}/{lid}")
    @ApiOperation(value = "用户通过标签id获取自己的私人文章信息列表", notes = "已经登录的用户通过标签的id获取这个私人文章的详细信息列表， 会toekn二次校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "lid", value = "标签的id", required = true, dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getPrivateByLid(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "lid") int lid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleInfomationService.getByLid(lid, Article.PRIVETA_ARTICLE);
        }else {
            return  null;
        }
    }


    /**
     * 用户通过标签id获取草稿类型的文章信息
     * @param req 包含token的对象
     * @param uid 用户id
     * @param lid 标签id
     * @return
     */
    @GetMapping("getDraftByLid/{uid}/{lid}")
    @ApiOperation(value = "用户通过标签id获取自己的草稿文章信息列表", notes = "已经登录的用户通过标签的id获取这个草稿文章的详细信息列表， 会toekn二次校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "lid", value = "标签的id", required = true, dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getDraftByLid(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "lid") int lid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleInfomationService.getByLid(lid, Article.DRAFT_ARTICLE);
        }else {
            return  null;
        }
    }


    /**
     * 管理员通过标签的id获取私有文章信息
     * @param lid 标签id
     * @return
     */
    @GetMapping("admin/getPrivateByLid/{lid}")
    @ApiOperation(value = "管理员通过标签的id获取的私人文章信息",notes = "管理员通过标签的id获取的私人文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lid", required = true, value = "标签的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> adminGetPriavteByLid(@PathVariable(value = "lid") int lid){
        return articleInfomationService.getByLid(lid, Article.PRIVETA_ARTICLE);
    }


    /**
     * 管理员通过标签的id获取草稿箱文章信息
     * @param lid 标签id
     * @return
     */
    @GetMapping("admin/getDraftByLid/{lid}")
    @ApiOperation(value = "管理员通过标签的id获取的草稿文章信息",notes = "管理员通过标签的id获取的草稿文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lid", required = true, value = "标签的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> adminGetDraftByLid(@PathVariable(value = "lid") int lid){
        return articleInfomationService.getByLid(lid, Article.DRAFT_ARTICLE);
    }


    /**
     * 通过分类id获取公开文章信息
     * @param cid 分类的id
     * @return
     */
    @GetMapping(value = "getPublicByCid/{cid}")
    @ApiOperation(value = "获取这个分类下的公开文章信息", notes = "通过分类id获取这个分类下的公开文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value = "二级分类的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getPublicByCid(@PathVariable(value = "cid") int cid){
        return articleInfomationService.getByCid(cid, Article.PUBLIC_ARTICLE);
    }


    /**
     * 用户通过分类的id获取私有文章信息
     * @param req 包含token的请求对象
     * @param uid 用户id
     * @param cid 分类id
     * @return
     */
    @GetMapping(value = "getPrivateByCid/{uid}/{cid}")
    @ApiOperation(value = "用户通过分类id获取自己的私人文章信息列表", notes = "已经登录的用户通过分类的id获取这个私人文章的详细信息列表， 会toekn二次校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "cid", value = "分类的id", required = true, dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getPrivateByCid(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "cid") int cid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleInfomationService.getByCid(cid, Article.PRIVETA_ARTICLE);
        }else {
            return null;
        }
    }



    /**
     * 用户通过分类的id获取草稿类型文章信息
     * @param req 包含token的请求对象
     * @param uid 用户id
     * @param cid 分类id
     * @return
     */
    @GetMapping(value = "getDraftByCid/{uid}/{cid}")
    @ApiOperation(value = "用户通过分类id获取自己的草稿文章信息列表", notes = "已经登录的用户通过分类的id获取这个草稿文章的详细信息列表， 会toekn二次校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "cid", value = "分类的id", required = true, dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> getDraftByCid(HttpServletRequest req, @PathVariable(value = "uid") int uid, @PathVariable(value = "cid") int cid){
        if (CheckUserUtil.isUser(req, uid)){
            return articleInfomationService.getByCid(cid, Article.DRAFT_ARTICLE);
        }else {
            return null;
        }
    }


    /**
     * 管理员通过分类的id获取私有类型的文章
     * @param cid 分类的id
     * @return
     */
    @GetMapping(value = "admin/getPrivateByCid/{cid}")
    @ApiOperation(value = "管理员通过分类的id获取分类下的私人文章信息",notes = "管理员通过分类的id获取分类下的私人文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value = "分类的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> adminGetPrivateByCid(@PathVariable(value = "cid") int cid){
        return articleInfomationService.getByCid(cid, Article.PRIVETA_ARTICLE);
    }

    /**
     * 管理员通过分类的id获取草稿类型的文章
     * @param cid 分类的id
     * @return
     */
    @GetMapping(value = "admin/getDraftByCid/{cid}")
    @ApiOperation(value = "管理员通过分类的id获取分类下的草稿文章信息",notes = "管理员通过分类的id获取分类下的草稿文章信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", required = true, value = "分类的id", dataType = "int", paramType = "path")
    })
    public List<ArticleInfomation> adminGetDraftByCid(@PathVariable(value = "cid") int cid){
        return articleInfomationService.getByCid(cid, Article.DRAFT_ARTICLE);
    }



    /**
     * 更新文章信息
     * @param req
     * @param articleInfomation
     * @return
     */
    @PostMapping("update")
    @ApiOperation(value = "用户修改文章信息", notes = "保存用户对分类标签等信息进行的修改", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aid", required = true, value = "文章的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "cid", required = true, value = "二级分类的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "lids", required = false, value = "文章标签对象", dataType = "ArticleLabel", paramType = "query")
    })
    public MessageResult update(HttpServletRequest req, @RequestBody ArticleInfomation articleInfomation){
        boolean isUser = CheckUserUtil.isUser(req, articleInfomation.getUid());
        if (isUser){
            return articleInfomationService.update(articleInfomation);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }


}
