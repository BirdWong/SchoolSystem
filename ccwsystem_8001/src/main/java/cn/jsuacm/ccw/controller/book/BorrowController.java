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
     * @param bid 书籍id
     * @param uid 用户id
     * @return
     */
    @PutMapping("addBorrow")
    @ApiOperation(value = "用户申请借书", notes = "用户申请借书",httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bid", required = true, value = "图书id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "uid" , required = true, value = "用户id", dataType = "int", paramType = "query")
    })
    public MessageResult addBorrow(HttpServletRequest req, @RequestParam(value = "bid") int bid, @RequestParam("uid") int uid){
        if (CheckUserUtil.isUser(req, uid)){
            return borrowService.addBorrow(bid, uid);
        }else {
            return new MessageResult(false, "非本人操作");
        }
    }







    /**
     * 管理员通过一次借书申请
     * @param id
     * @return
     */
    @GetMapping(value = "admin/passApply/{id}")
    @ApiOperation(value = "管理员通过一条借书记录通过申请", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "借阅的记录id", required = true, dataType = "int", paramType = "path")
    })
    public MessageResult passApply(@PathVariable(value = "id") int id){
        return borrowService.passApply(id);
    }



    /**
     * 管理员回绝一次借书申请
     * @param id
     * @return
     */
    @GetMapping(value = "admin/refuseApply/{id}")
    @ApiOperation(value = "管理员回绝一条借书记录通过申请", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "借阅的记录id", required = true, dataType = "int", paramType = "path")
    })
    public MessageResult refuseApply(@PathVariable(value = "id") int id){
        return borrowService.refuseApply(id);
    }




    /**
     * 归还图书
     * @param id
     * @return
     */
    @GetMapping(value = "admin/hasReturn/{id}")
    @ApiOperation(value = "管理员设置某条记录由借阅中改为以归还", httpMethod = "GET")
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
    @ApiOperation(value = "用户查看自己的借阅记录", httpMethod = "GET")
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
     * 用户查看自己未审批/未归还的借阅记录
     * @param uid 用户id
     * @return
     */
    @GetMapping(value = "getBorrowingByUid/{uid}")
    @ApiOperation(value = "用户查看自己未审批/未归还的借阅记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", required = true, value = "用户的id", dataType = "int", paramType = "path")
    })
    public List<Borrow> getBorrowingByUid(HttpServletRequest req,@PathVariable(value = "uid")int uid){
        if (CheckUserUtil.isUser(req, uid)) {
            return borrowService.getBorrowingByUid(uid);
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
    @ApiOperation(value = "管理员根据用户的账号搜索用户的借书记录",httpMethod = "GET")
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
    @ApiOperation(value = "管理员根据用户的账号搜索用户的还未归还的借书记录",httpMethod = "GET")
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
    @ApiOperation(value = "管理员根据图书的id搜索这本书的借阅记录", httpMethod = "GET")
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
    @ApiOperation(value = "管理员根据图书的id获取这本书的未归还的借阅记录", httpMethod = "GET")
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
    @ApiOperation(value = "管理员分页查看所有的借书记录", httpMethod = "GET")
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
    @ApiOperation(value = "管理员分页获取图书的未归还的借阅记录", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", required = true, value = "当前页", dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面的大小", dataType = "int")
    })
    public PageResult<Borrow> getBorrowingPages(@PathVariable(value = "current") int current, @PathVariable(value = "pageSize") int pageSize){
        return borrowService.getBorrowingPages(current, pageSize);
    }

}


