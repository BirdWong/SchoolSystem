package cn.jsuacm.ccw.service.book;

import cn.jsuacm.ccw.pojo.book.Book;
import cn.jsuacm.ccw.pojo.enity.BookEsEmpty;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;

/**
 * @ClassName EsBookService
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 10:57
 */
public interface EsBookService {

    /**
     * 保存一个图书信息
     *
     * @param book
     * @return
     */
    public boolean saveBook(Book book);


    /**
     * 更新剩余数量
     *
     * @param id
     * @param status
     * @return
     */
    public boolean changeUse(int id, int status);


    /**
     * 通过id删除一个es文档
     *
     * @param id 文章的id ， 同时也是es的标识id
     * @return
     */
    public boolean deleteById(int id);


    /**
     * 通过关键字搜索书名
     *
     * @param keyWord  关键字
     * @param current  当前页
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<BookEsEmpty> searchByName(String keyWord, int current, int pageSize);


    /**
     * 通过书籍名称， 作者名， 出版社名称查找书籍
     * @param keyWord
     * @param current
     * @param pageSize
     * @return
     */
    public PageResult<BookEsEmpty> searchByMore(String keyWord, int current, int pageSize);
}