package cn.jsuacm.ccw.service.book;

import cn.jsuacm.ccw.pojo.book.Borrow;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @ClassName BorrowService
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 17:47
 */
public interface BorrowService extends IService<Borrow>{

    /**
     * 添加一个借阅记录
     * @param bid 图书记录
     * @param accountNumber 账号
     * @return
     */
    public MessageResult addBorrow(int bid, String accountNumber);


    /**
     * 修改借书记录为还书
     * @param id 借阅id
     * @return
     */
    public MessageResult hasReturn(int id);


    /**
     * 用户获取自己的借书记录
     * @param uid 用户id
     * @return
     */
    public List<Borrow> getByUid(int uid);


    /**
     * 用户获取自己未归还的借书记录
     * @param uid 用户id
     * @return
     */
    public List<Borrow> getBorrowingByUid(int uid);

    /**
     * 管理员根据用户的账户搜索用户的借书记录
     * @param accountNumber 账户
     * @return
     */
    public List<Borrow> getByAccountNumber(String accountNumber);


    /**
     * 管理员根据用户的账户搜索没有归还的记录
     * @param accountNumber 账户
     * @return
     */
    public List<Borrow> getBorrowingByAccountNumber(String accountNumber);


    /**
     * 管理员根据书籍id查看借阅记录
     * @param bid 书籍id
     * @return
     */
    public List<Borrow> getByBid(int bid);


    /**
     * 管理员根据书id查找未归还的记录
     * @param bid 书籍id
     * @return
     */
    public List<Borrow> getBorrowingByBid(int bid);

    /**
     * 分页查询所有借阅记录
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<Borrow> getPages(int current, int pageSize);


    /**
     * 分页查询所有未归还的记录， 并且按照借书时长排序
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    public PageResult<Borrow> getBorrowingPages(int current, int pageSize);


}
