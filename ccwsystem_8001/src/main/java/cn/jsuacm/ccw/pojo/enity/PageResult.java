package cn.jsuacm.ccw.pojo.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageResult
 * @Description 分页返回实体
 * @Author h4795
 * @Date 2019/06/19 21:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable{
    //总记录数
    private Long tatolSize;
    //当前页码
    private Long row;
    //页面内容个数
    private Long pageSize;
    //页面内容
    private List<T> pageContext;

}
