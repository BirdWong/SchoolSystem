package cn.jsuacm.ccw.pojo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName Label
 * @Description TODO
 * @Author h4795
 * @Date 2019/06/22 16:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Label implements Serializable {

    /**
     * ID
     */
    @TableId(value = "lid", type = IdType.AUTO)
    private int lid;


    /**
     * 标签名称
     */
    @TableField(value = "lname", strategy = FieldStrategy.NOT_NULL)
    private String lname;


    /**
     * 用户的id
     */
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    private int uid;
}
