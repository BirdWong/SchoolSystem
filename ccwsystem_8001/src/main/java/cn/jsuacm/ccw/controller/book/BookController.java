package cn.jsuacm.ccw.controller.book;

import cn.jsuacm.ccw.pojo.book.Book;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.book.BookService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName BookController
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 17:22
 */
@RestController
@RequestMapping(value = "book")
public class BookController {

    @Autowired
    private BookService bookService;


    /**
     * 通过isbn信息从豆瓣获取信息添加书籍
     * 豆瓣接口文档
     * https://douban-api-docs.zce.me/
     *
     * 目前使用接口代理
     * https://douban.uieee.com
     * @param isbn
     * @return
     */
    @PutMapping("admin/addByIsbn")
    @ApiOperation(value = "通过书籍的isbn信息从豆瓣获取信息添加图书", notes = "通过isbn信息从豆瓣接口查询到书籍的部分信息保存到数据库， 如果数据库中已经有重复的isbn将保存失败， 返回信息很重要，建议显示给用户，在后期第三方接口失效后可以给用户进行排查", httpMethod = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isbn", required = true, value = "书籍的isbn信息", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "size", required = true, value = "书籍拥有的数量", dataType = "int", paramType = "query")
    })
    public MessageResult addBookByIsbn(@RequestParam(value = "isbn") String isbn, @RequestParam(value = "size") int size){
        return bookService.addBookForIsbn(isbn, size);
    }


    /**
     * 通过用户手动添加书籍信息
     * @param book
     * @return
     */
    @PostMapping("admin/addByInfo")
    @ApiOperation(value = "用户通过手动输入书籍信息添加书籍", notes = "用户手动输入添加书籍信息，请注意对应的内容格式尽量与isbn添加的格式信息相同", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "title", required = true, value = "书籍名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "author", required = true, value = "作者名称，多位作者的话用英文逗号分割','", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "publisher", required = true, value = "出版社名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "price", required = true, value = "价格,精确到分，单位元。 示例：price：52.03元 ", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "pages", required = true, value = "书籍页数", dataType = "int", paramType = "query"),
            @ApiImplicitParam( name = "size", required = true, value = "书籍拥有的数量", dataType = "int", paramType = "query")
    })
    public MessageResult addBookByInfo(@RequestBody Book book){
        return bookService.addBookForInfo(book);
    }


    /**
     * 更新书籍
     * @param book
     * @return
     */
    @PostMapping("admin/update")
    @ApiOperation(value = "用户通过手动输入书籍信息修改书籍信息", notes = "用户通过手动输入书籍信息修改书籍信息，请注意对应的内容格式尽量与isbn添加的格式信息相同", httpMethod = "post")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "id", required = true, value = "书籍的id", dataType = "int", paramType = "query"),
            @ApiImplicitParam( name = "title", required = false, value = "书籍名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "author", required = false, value = "作者名称，多位作者的话用英文逗号分割','", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "publisher", required = false, value = "出版社名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "price", required = false, value = "价格,精确到分，单位元。 示例：price：52.03元 ", dataType = "string", paramType = "query"),
            @ApiImplicitParam( name = "pages", required = false, value = "书籍页数", dataType = "int", paramType = "query"),
            @ApiImplicitParam( name = "size", required = false, value = "书籍拥有的数量", dataType = "int", paramType = "query"),
            @ApiImplicitParam( name = "pubdate", required = false, value = "出版时间", dataType = "date", paramType = "query"),
            @ApiImplicitParam(name = "isbn", required = false, value = "书籍的isbn信息", dataType = "string", paramType = "query")
    })
    public MessageResult update(@RequestBody Book book){
        return bookService.updateBookInfo(book);
    }


    /**
     * 通过书籍id获取书籍信息
     * @param bid 书籍id
     * @return
     */
    @GetMapping(value = "getById/{bid}")
    @ApiOperation(value = "通过id获取书籍信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "bid", required = true, value = "书籍的id", dataType = "int", paramType = "path")
    })
    public Book getById(@PathVariable(value = "bid") int bid) {
        return bookService.getById(bid);
    }


    /**
     * 根据书籍的id删除书籍信息
     * @param bid
     * @return
     */
    @GetMapping(value = "admin/deleteById/{bid}")
    @ApiOperation(value = "通过书籍id删除书籍", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "bid", required = true, value = "书籍的id", dataType = "int", paramType = "path")
    })
    public MessageResult deleteById(@PathVariable(value = "bid") int bid) {
        return bookService.deleteById(bid);
    }



    /**
     * 根据书籍的id删除书籍信息
     * @param ids 书籍id数组
     * @return
     */
    @PutMapping(value = "admin/deleteByIds")
    @ApiOperation(value = "通过一组书籍id删除书籍", httpMethod = "put")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "ids", required = true, value = "书籍的id数组", dataType = "array", paramType = "query")
    })
    public MessageResult deleteById(@RequestParam(value = "ids") int[] ids) {
        return bookService.deleteByIds(ids);
    }


    /**
     * 分页获取书籍信息
     * @param current
     * @param pageSize
     * @return
     */
    @GetMapping(value = "getPages/{current}/{pageSize}")
    @ApiOperation(value = "分页获取书籍信息", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小", dataType = "int", paramType = "path")

    })
    public PageResult<Book> getPages(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize") int pageSize){
        return bookService.getPages(current, pageSize);
    }


}
