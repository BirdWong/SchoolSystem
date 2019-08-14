package cn.jsuacm.ccw.mapper.book;

import cn.jsuacm.ccw.pojo.book.Borrow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName BorrowMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 17:45
 */
@Mapper
@Component
public interface BorrowMapper extends BaseMapper<Borrow>{

    /**
     * 根据用户的id查询用户的借阅记录
     * @param uid 用户id
     * @param isBorrow 是否只用查询未归还记录
     * @return
     */
    @Select("<script>" +
            "select " +
            " id, bid, uid, begin_time, status, days " +
            " from borrow " +
            " where uid=#{uid} " +
            "<if test='isBorrow'>" +
            " and status &lt;&gt; 1 " +
            "</if>" +
            "</script>"
    )
    public List<Borrow> findByUid(@Param(value = "uid") int uid, @Param(value = "isBorrow") boolean isBorrow);






    /**
     * 根据用户的id查询用户的借阅记录
     * @param bid 书籍id
     * @param isBorrow 是否只用查询未归还记录
     * @return
     */
    @Select("<script>" +
            "select " +
            "id, bid, uid, begin_time, status, days " +
            " from borrow " +
            " where bid=#{bid} " +
            "<if test='isBorrow'>" +
            " and status &lt;&gt; 1 " +
            "</if>" +
            "</script>"
    )
    public List<Borrow> findByBid(@Param(value = "bid") int bid, @Param(value = "isBorrow") boolean isBorrow);





    @Select("select count(1) from borrow where bid=#{bid} and uid=#{uid} and status<>1")
    public Integer countByBidAndUid(@Param(value = "bid") int bid, @Param(value = "uid")int uid);




}
