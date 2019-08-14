package cn.jsuacm.ccw.mapper.book;

import cn.jsuacm.ccw.pojo.book.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @ClassName BookMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 17:01
 */
@Mapper
@Component
public interface BookMapper extends BaseMapper<Book>{

    /**
     * 判断是否已经有isbn
     * @param isbn
     * @return
     */
    @Select("select count(*) from book where isbn=#{isbn}")
    public int hasIsbn(@Param(value = "isbn") String isbn);

    /**
     * 判断是否有这个书籍记录
     * @param id
     * @return
     */
    @Select("select count(*) from book where id=#{id}")
    public int hasId(@Param(value = "id") int id);


    /**
     * 获取使用量
     * @param id
     * @return
     */
    @Select("select use_size as hasUse from book where id=#{id}")
    public Book getUse(@Param(value = "id") int id);

}
