package cn.jsuacm.ccw.controller;

import cn.jsuacm.ccw.pojo.enity.ArticleEsEmpty;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.EsArticleRepository;
import cn.jsuacm.ccw.service.EsArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @ClassName EsArticleController
 * @Description 使用es做搜索
 * @Author h4795
 * @Date 2019/07/23 17:38
 */
@RestController
@RequestMapping("search")
@Api(value = "search", description = "搜索内容接口")
public class EsArticleController {


    @Autowired
    private EsArticleService esArticleService;

    @Autowired
    private EsArticleRepository repository;

    /**
     * 通过一个关键词， 在文章的标题和文章的内容中做搜索
     * @param keyword 关键字
     * @param pageNumber 当前页面
     * @param pageSize  页面大小
     * @return 返回一个带页面信息的文章信息对象集合
     */
    @GetMapping("article/{pageNumber}/{pageSize}/{keyword}")
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
    @GetMapping("user/{pageNumber}/{pageSize}/{keyword}")
    @ApiOperation(value = "通过关键字搜索用户的名称查看他的文章", notes = "通过一个关键字，在文章的用户名内搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keywork", required = true, value = "需要搜索的关键字", dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "pageNumber", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int", paramType = "path")
    })
    public PageResult<ArticleEsEmpty> searchArticleByUsername(@PathVariable(value = "keyword") String keyword, @PathVariable(value = "pageNumber") Integer pageNumber, @PathVariable(value = "pageSize") Integer pageSize){
        return esArticleService.findByUsername(keyword, pageNumber, pageSize);
    }
}