package cn.jsuacm.gateway.pojo.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName MessageResult
 * @Description 状态返回实体
 * @Author h4795
 * @Date 2019/06/17 17:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResult {
    /**
     * 状态
     */
    private boolean status;
    /**
     * 信息
     */
    private String msg;
}
