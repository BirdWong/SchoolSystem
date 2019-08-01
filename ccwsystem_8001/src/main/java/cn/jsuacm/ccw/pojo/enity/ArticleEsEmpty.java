package cn.jsuacm.ccw.pojo.enity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ArticleEsEmpty
 * @Description Elastic
 * @Author h4795
 * @Date 2019/07/23 17:30
 */
@Data
@NoArgsConstructor
@Document(indexName = "article",type = "article", shards = 1,replicas = 0, refreshInterval = "-1")
public class ArticleEsEmpty implements Serializable{

    /**
     * 文章的id
     */
    @Field
    private Integer id;


    /**
     * 文章的标题
     */
    @Field
    private String title;


    /**
     * 文章的文字内容
     */
    @Field
    private String content;


    /**
     * 文章的状态
     */
    @Field
    private Integer status;

    /**
     * 文章所属类型
     */
    @Field
    private Integer kind;

    /**
     * 用户的id
     */
    @Field
    private Integer uid;


    /**
     * 分类的id
     */
    @Field
    private Integer cid;


    /**
     * 创建的时间
     */
    @Field
    //@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private Date createtime;

    /**
     * 用户名
     */
    @Field
    private String username;


    /**
     * 二级分类的名称
     */
    @Field
    private String category;
}
