package cn.jsuacm.ccw.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName Category
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/21 17:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    /**
     * 分类id
     */
    @TableId(value = "cid", type = IdType.AUTO)
    private int cid;


    /**
     * 分类父分类id
     */
    @TableField(value = "parent_id")
    private int parentId;

    /**
     * 分类名称
     */
    @TableField(value = "cname")
    private String cname;
}
