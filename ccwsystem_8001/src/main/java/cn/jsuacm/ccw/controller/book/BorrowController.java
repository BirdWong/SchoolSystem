package cn.jsuacm.ccw.controller.book;

import cn.jsuacm.ccw.pojo.book.Borrow;
import cn.jsuacm.ccw.pojo.enity.MessageResult;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.book.BorrowService;
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
 * @ClassName BorrowController
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/31 11:50
 */
@RestController
@RequestMapping(value = "borrow")
@Api(value = "图书的借阅操作")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;


    /**
     * 添加一条图书借阅记录
     * @param bid
     * @param accountNumber
     * @return
     */
    @PutMapping("admin/addBorrow")
    @ApiOperation(value = "管理员添加一个借阅记录", notes = "管理员通过用户的账户和图书的id添加一条借阅记录",httpMethod = "put")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bid", required = true, value = "图书id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "accountNumber" , required = true, value = "用户账户", dataType = "string", paramType = "query")
    })
    public MessageResult addBorrow(@RequestParam(value = "bid") int bid, @RequestParam("accountNumber") String accountNumber){
        return borrowService.addBorrow(bid, accountNumber);
    }


    /**
     * 归还图书
     * @param id
     * @return
     */
    @GetMapping(value = "admin/hasReturn/{id}")
    @ApiOperation(value = "管理员设置某条记录由借阅中改为以归还", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "借阅id", dataType = "int", paramType = "path")
    })
    public MessageResult hasReturn(@PathVariable(value = "id") int id){
        return borrowService.hasReturn(id);
    }

    /**
     * 用户查看自己的借阅记录
     * @param uid 用户id
     * @return
     */
    @GetMapping(value = "getByUid/{uid}")
    @ApiOperation(value = "用户查看自己的借阅记录", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<Borrow> getByUid(HttpServletRequest req,@PathVariable(value = "uid")int uid){
        if (CheckUserUtil.isUser(req, uid)) {
            return borrowService.getByUid(uid);
        }else {
            return null;
        }

    }




    /**
     * 用户查看自己未归还的借阅记录
     * @param uid 用户id
     * @return
     */
    @GetMapping(value = "getBorrowingByUid/{uid}")
    @ApiOperation(value = "用户查看自己未归还的借阅记录", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<Borrow> getBorrowingByUid(HttpServletRequest req,@PathVariable(value = "uid")int uid){
        if (CheckUserUtil.isUser(req, uid)) {
            return borrowService.getByUid(uid);
        }else {
            return null;
        }

    }


    /**
     * 管理员根据用户的账号搜索用户的借书记录
     * @param accountNumber 用户的账户
     * @return
     */
    @GetMapping(value = "admin/getByAccountNumber/{accountNumber}")
    @ApiOperation(value = "管理员根据用户的账号搜索用户的借书记录",httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountNumber", required = true, value = "用户的账户", dataType = "string", paramType = "path")
    })
    public List<Borrow> getByAccountNumber(@PathVariable(value = "accountNumber")String accountNumber){
        return borrowService.getByAccountNumber(accountNumber);
    }






    /**
     * 管理员根据用户的账号搜索用户的还未归还的借书记录
     * @param accountNumber 用户的账户
     * @return
     */
    @GetMapping(value = "admin/getBorrowingByAccountNumber/{accountNumber}")
    @ApiOperation(value = "管理员根据用户的账号搜索用户的还未归还的借书记录",httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountNumber", required = true, value = "用户的账户", dataType = "string", paramType = "path")
    })
    public List<Borrow> getBorrowingByAccountNumber(@PathVariable(value = "accountNumber")String accountNumber){
        return borrowService.getBorrowingByAccountNumber(accountNumber);
    }


    /**
     * 管理员根据图书的id获取这本书的借阅记录
     * @param bid 图书id
     * @return
     */
    @GetMapping(value = "admin/getByBid/{bid}")
    @ApiOperation(value = "管理员根据图书的id搜索这本书的借阅记录", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bid", required = true, value = "图书id", dataType = "int", paramType = "path")
    })
    public List<Borrow> getByBid(@PathVariable(value = "bid")int bid){
        return borrowService.getByBid(bid);
    }



    /**
     * 管理员根据图书的id获取这本书的未归还的借阅记录
     * @param bid 图书id
     * @return
     */
    @GetMapping(value = "admin/getBorrowingByBid/{bid}")
    @ApiOperation(value = "管理员根据图书的id获取这本书的未归还的借阅记录", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bid", required = true, value = "图书id", dataType = "int", paramType = "path")
    })
    public List<Borrow> getBorrowingByBid(@PathVariable(value = "bid")int bid){
        return borrowService.getBorrowingByBid(bid);
    }


    /**
     * 管理员分页获取图书的借阅记录
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping(value = "admin/getPages/{current}/{pageSize}")
    @ApiOperation(value = "管理员分页查看所有的借书记录", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int")
    })
    public PageResult<Borrow> getPages(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize") int pageSize){
        return borrowService.getPages(current, pageSize);
    }






    /**
     * 管理员分页获取图书的未归还的借阅记录
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    @GetMapping(value = "admin/getBorrowingPages/{current}/{pageSize}")
    @ApiOperation(value = "管理员分页获取图书的未归还的借阅记录", httpMethod = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int")
    })
    public PageResult<Borrow> getBorrowingPages(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize") int pageSize){
        return borrowService.getBorrowingPages(current, pageSize);
    }

}


