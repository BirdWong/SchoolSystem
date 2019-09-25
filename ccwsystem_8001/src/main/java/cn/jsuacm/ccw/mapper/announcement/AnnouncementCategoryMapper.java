package cn.jsuacm.ccw.mapper.announcement;

import cn.jsuacm.ccw.pojo.announcement.AnnouncementCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * @ClassName CategoryMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/21 22:12
 */
@Mapper
@Component
public interface AnnouncementCategoryMapper extends BaseMapper<AnnouncementCategory>{


    /**
     * 确认是否已经存在这个分类
     * @param category 分类名称
     * @return
     */
    @Select("select count(1) from announcement_category where name=#{name}")
    public int hasCategory(@Param(value = "name") String category);


    /**
     * 通过id确认自己这个id是否存在
     * @param id
     * @return
     */
    @Select("select count(1) from announcement_category where id=#{id}")
    public int hasCategory(@Param(value = "id") int id);


}
