package cn.jsuacm.ccw.mapper.projects;

import cn.jsuacm.ccw.pojo.projects.Collaborators;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @ClassName CollaboratorsMapper
 * @Description TODO
 * @Author h4795
 * @Date 2019/08/01 10:54
 */
@Component
@Mapper
public interface CollaboratorsMapper extends BaseMapper<Collaborators>{
}
