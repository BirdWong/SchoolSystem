package cn.jsuacm.ccw.service.book;

import cn.jsuacm.ccw.pojo.book.Book;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @ClassName BookService
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 17:01
 */
public interface BookService extends IService<Book> {

    /**
     * 通过isbn添加图书
     * @param isbn isbn信息
     * @param size 拥有的数量
     * @return
     */
    public MessageResult addBookForIsbn(String isbn, int size);


    /**
     * 用户手动输入书籍信息
     * @param book 书籍对象
     * @return
     */
    public MessageResult addBookForInfo(Book book);


    /**
     * 更新书籍信息
     * @param book 书籍对象
     * @return
     */
    public MessageResult updateBookInfo(Book book);


    /**
     * 通过id删除书籍信息
     * @param id 书籍id
     * @return
     */
    public MessageResult deleteById(int id);


    /**
     * 通过一组书籍id删除信息
     * @param ids 书籍id数组
     * @return
     */
    public MessageResult deleteByIds(int[] ids);


    /**
     * 借书
     * @param bid
     */
    public void hasBorrow(int bid);


    /**
     * 还书
     * @param bid
     */
    public void hasReturn(int bid);



    /**
     * 分页查看书籍
     * @param current
     * @param pageSize
     * @return
     */
    public PageResult<Book> getPages(int current, int pageSize);



}
