package cn.jsuacm.ccw.pojo.book;

import com.baomidou.mybatisplus.annotation.*;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName Borrow
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 17:24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName(value = "borrow")
@ApiModel(value = "借阅实体")
public class Borrow {



    @TableField(exist = false)
    public static short BORROWING = 0;

    @TableField(exist = false)
    public static short RETURN = 1;


    /**
     * 借阅id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "借阅id", dataType = "int")
    private int id;


    /**
     * 用户id
     */
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "用户的id", dataType = "int")
    private int uid;


    /**
     * 书籍的id
     */
    @TableField(value = "bid", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "书籍的id", dataType = "int")
    private int bid;


    /**
     * 开始借书的时间
     */
    @TableField(value = "begin_time", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "开始借阅的时间", dataType = "date")
    private Date beginTime;


    /**
     * 借书的状态
     */
    @ApiModelProperty(value = "借阅状态, 0:借阅中 1:归还", dataType = "short")
    @TableField(value = "status", strategy = FieldStrategy.NOT_NULL)
    private short status;


    /**
     * 一次借阅的时长
     */
    @ApiModelProperty(value = "一次借书的借书时长, 借阅中的没有时长", dataType = "int")
    @TableField(value = "days")
    private int days;


}
