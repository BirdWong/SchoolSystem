package cn.jsuacm.ccw.pojo.book;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName BookController
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/29 16:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "book")
@ApiModel(value = "图书实体")
public class Book {

    /**
     * 图书的id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "图书的id", dataType = "int")
    private int id;


    /**
     * 图书名称
     */
    @TableField(value = "title", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "图书的名称", dataType = "string", required = true)
    private String title;


    /**
     * 图书作者
     */
    @TableField(value = "author", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "作者姓名", dataType = "string", required = true)
    private String author;


    /**
     * 出版社
     */
    @TableField(value = "publisher", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "出版社", dataType = "string", required = true)
    private String publisher;


    /**
     * 出版时间
     */
    @TableField(value = "pubdate")
    @ApiModelProperty(value = "出版时间", dataType = "date")
    private Date pubdate;


    /**
     * 图书的isbn
     */
    @ApiModelProperty(value = "图书isbn", dataType = "string")
    @TableField(value = "isbn")
    private String isbn;


    /**
     * 价格
     */
    @ApiModelProperty(value = "价格", dataType = "string", required = true)
    @TableField(value = "price", strategy = FieldStrategy.NOT_NULL)
    private String price;


    /**
     * 页面数量
     */
    @ApiModelProperty(value = "页面数量", dataType = "int", required = true)
    @TableField(value = "pages", strategy = FieldStrategy.NOT_NULL)
    private int pages;


    /**
     * 书籍拥有数量
     */
    @TableField(value = "has_size", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "书籍拥有的数量",  dataType = "int", required = true)
    private int size;


    /**
     * 已经被借出去的数量
     */
    @TableField(value = "`use_size`", strategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "剩余的数量" ,dataType = "int", required = true)
    private int hasUse;

}
