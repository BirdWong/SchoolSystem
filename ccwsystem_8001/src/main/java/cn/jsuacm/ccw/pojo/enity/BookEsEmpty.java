package cn.jsuacm.ccw.pojo.enity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName BookEsEmpty
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 10:50
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "book", type = "book", shards = 1, replicas = 0, refreshInterval = "-1")
public class BookEsEmpty implements Serializable{

    /**
     * 书籍id
     */
    @Field
    private Integer id;


    /**
     * 书籍名称
     */
    @Field
    private String title;

    /**
     * 书籍作者
     */
    @Field
    private String author;

    /**
     * 出版社
     */
    @Field
    private String publisher;


    /**
     * 出版时间
     */
    @Field
    private Date pubdate;


    /**
     * isbn
     */
    @Field
    private String isbn;


    /**
     * 价格
     */
    @Field
    private String price;


    /**
     * 页数
     */
    @Field
    private Integer pages;


    /**
     * 拥有数量
     */
    @Field
    private Integer size;


    /**
     * 剩余数量
     */
    @Field()
    private Integer hasUse;
}
