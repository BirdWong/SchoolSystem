package cn.jsuacm.ccw.service.book.impl;

import cn.jsuacm.ccw.mapper.book.BookMapper;
import cn.jsuacm.ccw.mapper.book.BorrowMapper;
import cn.jsuacm.ccw.pojo.book.Book;
import cn.jsuacm.ccw.pojo.book.Borrow;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.book.BookService;
import cn.jsuacm.ccw.service.book.BorrowService;
import cn.jsuacm.ccw.service.book.EsBookService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName BorrowServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 17:48
 */
@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements BorrowService{

    @Autowired
    private BookService bookService;



    @Autowired
    private BorrowMapper borrowMapper;



    @Autowired
    private EsBookService esBookService;


    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate restTemplate;


    private String url = "http://JSUCCW-ZUUL-GATEWAY/user/getUserByAccountNumber/";

    /**
     * 添加一个借阅记录
     *
     * @param bid           图书记录
     * @param accountNumber 账号
     * @return
     */
    @Override
    public MessageResult addBorrow(int bid, String accountNumber) {

        //获取这本书的信息，确认是否还有剩余
        Book book = bookService.getById(bid);


        if (book == null){
            return new MessageResult(false, "没有这本书籍");
        }
        if (book.getSize() - book.getUse() == 0){
            return new MessageResult(false, "没有剩余书籍");
        }
        // 获取用户id， 同时确认用户确实存在
        MessageResult messageResult = getUid(accountNumber);
        if (!messageResult.isStatus()){
            return messageResult;
        }
        int uid = Integer.valueOf(messageResult.getMsg());
        Borrow borrow = new Borrow();
        borrow.setBid(bid);
        borrow.setBeginTime(new Date());
        borrow.setDays(0);
        borrow.setStatus(Borrow.BORROWING);
        borrow.setUid(uid);
        save(borrow);
        // 图书更新可用书目
        bookService.hasBorrow(bid);
        return new MessageResult(true, "借阅成功");
    }



    /**
     * 修改借书记录为还书
     *
     * @param id
     * @return
     */
    @Override
    public MessageResult hasReturn(int id) {
        Borrow borrow = borrowMapper.selectById(id);
        if (borrow == null){
            return  new MessageResult(false, "没有这条借阅记录");
        }
        if (borrow.getStatus() == Borrow.RETURN){
            return new MessageResult(false, "图书已经被归还了");
        }
        long days = getDays(borrow.getBeginTime());

        UpdateWrapper<Borrow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("days", days).set("status", Borrow.RETURN);
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (update){
            bookService.hasReturn(borrow.getBid());
            return new MessageResult(update, "归还成功");
        }else {
            return new MessageResult(update, "归还失败， 刷新后重试");
        }
    }

    /**
     * 用户获取自己的借书记录
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public List<Borrow> getByUid(int uid) {
        List<Borrow> borrows = borrowMapper.findByUid(uid, false);
        for (Borrow borrow : borrows){
            borrow.setDays(getDays(borrow.getBeginTime()));
        }
        return borrows;
    }

    /**
     * 用户获取自己未归还的借书记录
     *
     * @param uid 用户id
     * @return
     */
    @Override
    public List<Borrow> getBorrowingByUid(int uid) {
        List<Borrow> borrows = borrowMapper.findByUid(uid, true);
        for (Borrow borrow : borrows){
            borrow.setDays(getDays(borrow.getBeginTime()));
        }
        return borrows;
    }

    /**
     * 管理员根据用户的账户搜索用户的借书记录
     *
     * @param accountNumber 账户
     * @return
     */
    @Override
    public List<Borrow> getByAccountNumber(String accountNumber) {
        MessageResult messageResult = getUid(accountNumber);
        if (messageResult.isStatus()){
            int uid = Integer.valueOf(messageResult.getMsg());
            return getByUid(uid);
        }
        return null;
    }

    /**
     * 管理员根据用户的账户搜索没有归还的记录
     *
     * @param accountNumber 账户
     * @return
     */
    @Override
    public List<Borrow> getBorrowingByAccountNumber(String accountNumber) {
        MessageResult messageResult = getUid(accountNumber);
        if (messageResult.isStatus()){
            int uid = Integer.valueOf(messageResult.getMsg());
            return getBorrowingByUid(uid);
        }
        return null;
    }

    /**
     * 管理员根据书籍id查看借阅记录
     *
     * @param bid 书籍id
     * @return
     */
    @Override
    public List<Borrow> getByBid(int bid) {
        List<Borrow> borrows = borrowMapper.findByBid(bid, false);
        for (Borrow borrow : borrows){
            borrow.setDays(getDays(borrow.getBeginTime()));
        }
        return borrows;
    }

    /**
     * 管理员根据书id查找未归还的记录
     *
     * @param bid 书籍id
     * @return
     */
    @Override
    public List<Borrow> getBorrowingByBid(int bid) {
        List<Borrow> borrows = borrowMapper.findByBid(bid, true);
        for (Borrow borrow : borrows){
            borrow.setDays(getDays(borrow.getBeginTime()));
        }
        return borrows;
    }

    /**
     * 分页查询所有借阅记录
     *
     * @param current  当前页
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public PageResult<Borrow> getPages(int current, int pageSize) {
        IPage<Borrow> page = new Page<>();

        page.setCurrent(current);
        page.setSize(pageSize);
        IPage<Borrow> borrowIPage = borrowMapper.selectPage(page, null);
        List<Borrow> borrows = borrowIPage.getRecords();
        for (Borrow borrow : borrows){
            if (borrow.getStatus() == Borrow.BORROWING) {
                borrow.setDays(getDays(borrow.getBeginTime()));
            }
        }
        PageResult<Borrow> pageResult = new PageResult<>();
        pageResult.setRow(borrowIPage.getCurrent());
        pageResult.setPageSize(borrowIPage.getSize());
        pageResult.setTatolSize(borrowIPage.getTotal());
        pageResult.setPageContext(borrows);
        return pageResult;
    }

    /**
     * 分页查询所有未归还的记录， 并且按照借书时长排序
     *
     * @param current  当前页
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public PageResult<Borrow> getBorrowingPages(int current, int pageSize) {
        IPage<Borrow> page = new Page<>();

        page.setCurrent(current);
        page.setSize(pageSize);
        QueryWrapper<Borrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        IPage<Borrow> borrowIPage = borrowMapper.selectPage(page, queryWrapper);
        List<Borrow> borrows = borrowIPage.getRecords();
        for (Borrow borrow : borrows){
            borrow.setDays(getDays(borrow.getBeginTime()));
        }
        Collections.sort(borrows, new Comparator<Borrow>() {
            @Override
            public int compare(Borrow borrow, Borrow t1) {
                return borrow.getDays() - t1.getDays();
            }
        });
        PageResult<Borrow> pageResult = new PageResult<>();
        pageResult.setRow(borrowIPage.getCurrent());
        pageResult.setPageSize(borrowIPage.getSize());
        pageResult.setTatolSize(borrowIPage.getTotal());
        pageResult.setPageContext(borrows);
        return pageResult;
    }


    /**
     * 获取两个的时间长度
     * @param old 开始借阅的时间
     * @return
     */
    private int getDays(Date old) {
        Date now = new Date();
        return (int) (now.getTime() - old.getTime() / 1000 / 24 / 3600);
    }


    /**
     * 获取用户的id
     * @param accountNumber
     * @return
     */
    private MessageResult getUid(String accountNumber){
        // 获取用户id， 同时确认用户确实存在
        ResponseEntity<String> entity = restTemplate.getForEntity(url+accountNumber, String.class);
        try {
            Map<String,Object> values = new ObjectMapper().readValue(entity.getBody(), Map.class);
            if (values.get("uid") == null){
                return new MessageResult(false, "没有查找到这个用户:"+accountNumber);
            }
            return new MessageResult(true, String.valueOf(values.get("uid")));
        } catch (IOException e) {
            return new MessageResult(false, "服务器内部错误");
        }
    }

}
