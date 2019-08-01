package cn.jsuacm.ccw.pojo.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName ArticleComplex
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/31 15:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComplex implements Serializable{
    Article article;
    ArticleInfomation articleInfomation;
}
