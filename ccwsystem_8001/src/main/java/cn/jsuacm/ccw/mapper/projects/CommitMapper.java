package cn.jsuacm.ccw.mapper.projects;

import cn.jsuacm.ccw.pojo.projects.Commit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName CommitMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 10:53
 */
@Mapper
@Component
public interface CommitMapper extends BaseMapper<Commit>{
}
