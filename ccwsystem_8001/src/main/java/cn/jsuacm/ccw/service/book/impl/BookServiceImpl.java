package cn.jsuacm.ccw.service.book.impl;

import cn.jsuacm.ccw.mapper.book.BookMapper;
import cn.jsuacm.ccw.pojo.book.Book;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.book.BookService;
import cn.jsuacm.ccw.service.book.EsBookService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BookServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 17:03
 */
@Service
@CacheConfig(cacheNames = "book")
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService{

    @Autowired
    @Qualifier(value = "remoteRestTemplate")
    private RestTemplate restTemplate;



    @Autowired
    private BookMapper bookMapper;


    @Autowired
    private EsBookService esBookService;


    private String uri = "https://douban.uieee.com/v2/book/isbn/{isbn}?fields=author,publisher,isbn13,title,price,pages,pubdate,id";
    /**
     * 通过isbn添加图书
     *
     * @param isbn
     * @return
     */
    @Override
    public MessageResult addBookForIsbn(String isbn, int size) {
        if (bookMapper.hasIsbn(isbn) != 0){
            return new MessageResult(false, "已经存在此书籍");
        }

        try {
            String url = uri.replace("{isbn}", isbn);

            Map<String,Object> values = null;
            try {
                ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
                values = new ObjectMapper().readValue(entity.getBody(), Map.class);
            }catch (HttpClientErrorException e){
                return new MessageResult(false, "isbn输入错误或者没有此书籍");
            }


            Book book = new Book();
            book.setTitle(String.valueOf(values.get("title")));
            List<String> authors = (List<String>)values.get("author");
            StringBuilder author = new StringBuilder();
            boolean sign = true;
            for (String str : authors){
                if (sign){
                    sign = false;
                }else {
                    author.append(",");
                }
                author.append(str);
            }
            book.setAuthor(author.toString());
            book.setIsbn(isbn);
            book.setPages(Integer.valueOf(String.valueOf(values.get("pages"))));
            book.setPrice(String.valueOf(values.get("price")));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            book.setPubdate(dateFormat.parse(String.valueOf(values.get("pubdate"))));
            book.setPublisher(String.valueOf(values.get("publisher")));
            book.setSize(size);
            book.setHasUse(0);
            if (hasBook(book)){
                return new MessageResult(false, "已经存在此书");
            }
            save(book);
            esBookService.saveBook(book);
            return new MessageResult(true, book.getId()+"");

        } catch (IOException e) {
            return new MessageResult(false, "请求错误，请检查isbn是否填写正确或者一个小时后尝试, 如果超过24小时不可用请联系维护人员进行第三方接口检测：文档：https://douban-api-docs.zce.me/  接口代理：https://douban.uieee.com");
        } catch (ParseException e) {
            return new MessageResult(false, "格式转换错误，请重试");
        }

    }

    /**
     * 用户手动输入书籍信息
     *
     * @param book
     * @return
     */
    @Override
    public MessageResult addBookForInfo(Book book) {
        if (checkBookInfo(book)){
            if (hasBook(book)){
                return new MessageResult(false, "已经存在此书信息");
            }
            save(book);
            esBookService.saveBook(book);
            return new MessageResult(true, book.getId() + "");
        }
        return new MessageResult(false, "信息填写错误");
    }

    /**
     * 更新书籍信息
     *
     * @param book
     * @return
     */
    @Override
    public MessageResult updateBookInfo(Book book) {
        if (checkBookInfo(book) && bookMapper.hasId(book.getId()) != 0) {
            if (hasBook(book)) {
                return new MessageResult(false, "更新的信息与其他书籍重复");
            } else {
                Book use = bookMapper.getUse(book.getId());
                book.setHasUse(use.getHasUse());
                updateById(book);
                esBookService.saveBook(book);
                return new MessageResult(true, book.getId()+"");
            }
        }else {
            return new MessageResult(false, "信息填写错误");
        }
    }

    /**
     * 通过id删除书籍信息
     *
     * @param id 书籍id
     * @return
     */
    @Override
    public MessageResult deleteById(int id) {

        Book book = getById(id);
        if (book == null){
            return new MessageResult(false, "没有这本书籍");
        }
        if (book.getHasUse() != 0) {
            return new MessageResult(false, "还有书籍未归还");
        } else {
            try {
                removeById(id);
                esBookService.deleteById(id);
            }catch (Exception e){
                return new MessageResult(false, "删除失败");
            }


            return new MessageResult(true, "删除成功");
        }
    }
    /**
     * 通过一组书籍id删除信息
     *
     * @param ids
     * @return
     */
    @Override
    public MessageResult deleteByIds(int[] ids) {

        StringBuilder idstr = new StringBuilder();
        boolean sign = true;
        for (int id : ids){
            MessageResult messageResult = deleteById(id);
            if (!messageResult.isStatus()){
                if (sign){
                    sign = false;
                }else {
                    idstr.append(",");
                }
                idstr.append(id);
            }
        }
        if (idstr.length() == 0){
            return new MessageResult(true, "删除成功");
        }else {
            return new MessageResult(false, "部分未删除成功，请确认以下id书籍存在或已经被全部归还:"+idstr.toString());
        }
    }

    /**
     * 借书
     *
     * @param bid
     */
    @Override
    public void hasBorrow(int bid) {
        Book book = getById(bid);
        book.setHasUse(book.getHasUse() + 1);
        updateById(book);
        esBookService.changeUse(bid, 1);
    }

    /**
     * 还书
     *
     * @param bid
     */
    @Override
    public void hasReturn(int bid) {
        Book book = getById(bid);
        book.setHasUse(book.getHasUse() - 1);
        updateById(book);
        esBookService.changeUse(bid, -1);
    }

    /**
     * 分页查看书籍
     *
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    @Cacheable
    public PageResult<Book> getPages(int current, int pageSize) {
        IPage<Book> iPage = new Page<>();
        iPage.setSize(pageSize);
        iPage.setCurrent(current);
        IPage<Book> bookIPage = bookMapper.selectPage(iPage, new QueryWrapper<Book>());
        PageResult<Book> pageResult = new PageResult<>();
        pageResult.setPageContext(bookIPage.getRecords());
        pageResult.setTatolSize(bookIPage.getTotal());
        pageResult.setPageSize(bookIPage.getSize());
        pageResult.setRow(bookIPage.getCurrent());
        return pageResult;
    }



    /**
     * 检查是否已经存在这本书
     * @param book
     * @return
     */
    private boolean hasBook(Book book){

        QueryWrapper<Book> bookQueryWrapper = new QueryWrapper<>();
        bookQueryWrapper.eq("title", book.getTitle()).eq("publisher", book.getPublisher()).eq("pages", book.getPages());
        Integer count = bookMapper.selectCount(bookQueryWrapper);
        if (count != 0){
            return true;
        }

        return false;
    }



    /**
     * 校验图书信息
     * @param book
     * @return
     */
    private boolean checkBookInfo(Book book){
        if (!checkStringInfo(book.getTitle()) || !checkStringInfo(book.getAuthor()) || !checkStringInfo(book.getPrice()) || !checkStringInfo(book.getPublisher()) || book.getPages() == 0 || book.getSize() == 0){
            return false;
        }else {
            return true;
        }


    }


    /**
     * 校验字符串信息
     * @param info
     * @return
     */
    private boolean checkStringInfo(String info){
        if (info == null ||  info.length() == 0 || info.length() > 255){
            return false;
        }else {
            return true;
        }
    }
}
