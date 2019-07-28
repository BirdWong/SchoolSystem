package cn.jsuacm.ccw.pojo.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName MessageResult
 * @Description 状态返回实体类
 * @Author h4795
 * @Date 2019/06/19 15:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResult implements Serializable{
    /**
     * 状态
     */
    private boolean status;
    /**
     * 信息
     */
    private String msg;
}
