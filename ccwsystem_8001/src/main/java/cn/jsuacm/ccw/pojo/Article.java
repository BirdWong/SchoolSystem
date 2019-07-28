package cn.jsuacm.ccw.pojo;

import cn.jsuacm.ccw.pojo.enity.MessageResult;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * @ClassName Article
 * @Description 文章实体
 * @Author h4795
 * @Date 2019/06/18 21:40
 */
@Data
@TableName(value = "article")
public class Article implements Serializable{

    /**
     * 类型属于用户
     */
    @TableField(exist = false)
    public static final int USER_ARTICLE = 0;


    /**
     * 状态种类集合
     */
    @TableField(exist = false)
    public static final Set<Integer> STATUS_SET = new HashSet<>(Arrays.asList(new Integer[]{-1,0,1}));


    /**
     * 文章类型公开
     */
    public static final int PUBLIC_ARTICLE = 1;

    /**
     * 文章类型私有
     */
    public static final int PRIVETA_ARTICLE = -1;


    public static final int DRAFT_ARTICLE = 0;

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
    @TableField(value = "html_content", strategy = FieldStrategy.NOT_NULL)
    private String htmlContent;


    /**
     * markdown语法内容
     */
    @TableField(value = "content", strategy = FieldStrategy.NOT_NULL)
    private String content;

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

    /**
     * 校验文章格式等是否出现问题
     * @param article
     * @param kind
     * @return
     */
    public static MessageResult checkArticle(Article article, int kind){
        if (article.getTitle() == null || article.getTitle().length() == 0 || article.getTitle().length() > 101){
            return new MessageResult(false, "请正确填写标题，并且长度不能超过100");
        }
        if (article.getKind() != kind){
            return new MessageResult(false, "权限不足");
        }
        if (!Article.STATUS_SET.contains(article.getStatus())){
            return new MessageResult(false, "文章状态出现异常");
        }
        if (article.getContent() == null || article.getContent().length() == 0 || article.getHtmlContent() == null || article.getHtmlContent().length() == 0){
            return new MessageResult(false, "请填写文章内容");
        }
        return new MessageResult(true, "校验成功");
    }

}