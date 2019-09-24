package cn.jsuacm.ccw.mapper.announcement;

import cn.jsuacm.ccw.pojo.announcement.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName AnnouncementMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/09/23 20:57
 */
@Mapper
@Component
public interface AnnouncementMapper extends BaseMapper<Announcement>{

}
