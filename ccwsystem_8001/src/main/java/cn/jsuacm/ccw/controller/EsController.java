package cn.jsuacm.ccw.controller;

import cn.jsuacm.ccw.pojo.enity.ArticleEsEmpty;
import cn.jsuacm.ccw.pojo.enity.BookEsEmpty;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.blog.EsArticleRepository;
import cn.jsuacm.ccw.service.blog.EsArticleService;
import cn.jsuacm.ccw.service.book.EsBookService;
import cn.jsuacm.ccw.util.CheckUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName EsArticleController
 * @Description 使用es做搜索
 * @Author h4795
 * @Date 2019/07/23 17:38
 */
@RestController
@RequestMapping("search")
@Api(value = "search", description = "搜索内容接口")
public class EsController {


    @Autowired
    private EsArticleService esArticleService;



    @Autowired
    private EsBookService esBookService;

    @Autowired
    private EsArticleRepository repository;

    /**
     * 通过一个关键词， 在文章的标题和文章的内容中做搜索
     * @param keyword 关键字
     * @param pageNumber 当前页面
     * @param pageSize  页面大小
     * @return 返回一个带页面信息的文章信息对象集合
     */
    @GetMapping("article/title/{pageNumber}/{pageSize}/{keyword}")
    @ApiOperation(value = "通过关键词搜索标题和内容", notes = "通过一个关键词， 在文章的标题和文章的内容中做搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keywork", required = true, value = "需要搜索的关键字", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "pageNumber", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int", paramType = "path")
    })
    public PageResult<ArticleEsEmpty> searchArticleByTitleOrContent(@PathVariable(value = "keyword") String keyword, @PathVariable(value = "pageNumber") Integer pageNumber, @PathVariable(value = "pageSize") Integer pageSize) {
        return esArticleService.findArticle(keyword, pageNumber, pageSize);
    }



    /**
     * 通过一个关键字，在文章的用户名内搜索
     * @param keyword 关键字
     * @param pageNumber 当前页面
     * @param pageSize 页面的大小
     * @return 返回一个带页面信息的文章信息对象集合
     */
    @GetMapping("article/user/{pageNumber}/{pageSize}/{keyword}")
    @ApiOperation(value = "通过关键字搜索用户的名称查看他的文章", notes = "通过一个关键字，在文章的用户名内搜索", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keywork", required = true, value = "需要搜索的关键字", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "pageNumber", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int", paramType = "path")
    })
    public PageResult<ArticleEsEmpty> searchArticleByUsername(@PathVariable(value = "keyword") String keyword, @PathVariable(value = "pageNumber") Integer pageNumber, @PathVariable(value = "pageSize") Integer pageSize){
        return esArticleService.findByUsername(keyword, pageNumber, pageSize);
    }


    /**
     * 通过关键字搜索书名
     * @param keyword 关键字
     * @param pageNumber 当前页面
     * @param pageSize 页面的大小
     * @return 返回一个带页面信息的书籍信息对象集合
     */
    @GetMapping("book/title/{pageNumber}/{pageSize}/{keyword}")
    @ApiOperation(value = "通过关键字搜索书名", httpMethod = "get")@ApiImplicitParams({
            @ApiImplicitParam(name = "keywork", required = true, value = "需要搜索的关键字", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "pageNumber", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int", paramType = "path")
    })
    public PageResult<BookEsEmpty> searchBookByTitle(@PathVariable(value = "keyword") String keyword, @PathVariable(value = "pageNumber") Integer pageNumber, @PathVariable(value = "pageSize") Integer pageSize){
        return  esBookService.searchByName(keyword, pageNumber, pageSize);
    }



    /**
     * 通过关键字搜索书名/出版社/作者
     * @param keyword 关键字
     * @param pageNumber 当前页面
     * @param pageSize 页面的大小
     * @return 返回一个带页面信息的书籍信息对象集合
     */
    @GetMapping("book/more/{pageNumber}/{pageSize}/{keyword}")
    @ApiOperation(value = "通过关键字搜索书名/出版社/作者", httpMethod = "get")@ApiImplicitParams({
            @ApiImplicitParam(name = "keywork", required = true, value = "需要搜索的关键字", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "pageNumber", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int", paramType = "path")
    })
    public PageResult<BookEsEmpty> searchBookByMore(@PathVariable(value = "keyword") String keyword, @PathVariable(value = "pageNumber") Integer pageNumber, @PathVariable(value = "pageSize") Integer pageSize){
        return  esBookService.searchByMore(keyword, pageNumber, pageSize);
    }




    @PutMapping("updateUsername")
    @ApiOperation(value = "用户修改用户昵称后同时也要更改es中的用户名信息", notes = "用户修改用户昵称后同时也要更改es中的用户名信息， 此接口和user模块中修改用户名接口同时使用", httpMethod = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户的id", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "用户的新的昵称", required = true, dataType = "string", paramType = "query")
    })
    public MessageResult updateUserName(HttpServletRequest req, @RequestParam(value = "uid") int uid, @RequestParam(value = "username") String username){
        if (CheckUserUtil.isUser(req, uid)){
            return esArticleService.updateUsername(uid, username);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }
}