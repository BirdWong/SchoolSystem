package cn.jsuacm.ccw.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.*;

/**
 * @ClassName Article
 * @Description 文章实体
 * @Author h4795
 * @Date 2019/06/18 21:40
 */
@Data
@TableName(value = "article")
public class Article {

    /**
     * 类型属于用户
     */
    @TableField(exist = false)
    public static int USER_ARTICLE = 0;


    /**
     * 状态种类集合
     */
    @TableField(exist = false)
    public static Set<Integer> STATUS_SET = new HashSet<>(Arrays.asList(new Integer[]{-1,1}));


    /**
     * 文章类型公开
     */
    public static int PUBLIC_ARTICLE = 1;

    /**
     * 文章类型私有
     */
    public static int PRIVETA_ARTICLE = -1;


    /**
     * 文章的id
     */
    @TableId(value = "aid", type = IdType.AUTO)
    private int aid;

    /**
     * 文章的标题
     */
    @TableField(value = "title", strategy = FieldStrategy.NOT_NULL)
    private String title;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", strategy = FieldStrategy.NOT_NULL)
    private Date createTime;

    /**
     * 修改的时间
     */
    @TableField(value = "modify_time", strategy = FieldStrategy.NOT_NULL)
    private Date modifyTime;


    /**
     * 文章目前的状态
     * -1-草稿
     * 1-发布
     */
    @TableField(value = "status", strategy = FieldStrategy.NOT_NULL)
    private int status;


    /**
     * html文章内容
     */
    @TableField(value = "html_context", strategy = FieldStrategy.NOT_NULL)
    private String htmlContext;


    /**
     * markdown语法内容
     */
    @TableField(value = "context", strategy = FieldStrategy.NOT_NULL)
    private String context;

    /**
     * 文章查看量
     */
    @TableField(value = "views", strategy = FieldStrategy.NOT_NULL)
    private int views;

    /**
     * 所属类型
     *  - 0： 用户文章
     *
     */
    @TableField(value = "kind", strategy = FieldStrategy.NOT_NULL)
    private int kind;

    /**
     * 作者id
     */
    @TableField(value = "uid", strategy = FieldStrategy.NOT_NULL)
    private int uid;
}
