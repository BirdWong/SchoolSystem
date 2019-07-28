import cn.jsuacm.ccw.mapper.ArticleMapper;
import cn.jsuacm.ccw.pojo.Article;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @ClassName MyBatisPlusTest
 * @Description mybatis测试
 * @Author h4795
 * @Date 2019/06/20 20:13
 */
@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = SpringBootApplicationTest.class)
public class MyBatisPlusTest {

    @Autowired
    private ArticleMapper articleMapper;

    @Test
    public void insertTest() {
        for (int i = 1; i < 12; i++) {


            Article article = new Article();
            article.setTitle("test");
            article.setStatus(1);
            article.setKind(Article.USER_ARTICLE);
            article.setHtmlContent("html");
            article.setContent("123");
            article.setUid(1);
            article.setViews(0);
            article.setModifyTime(new Date());
            article.setCreateTime(new Date());
            articleMapper.insert(article);
            System.out.println(article.getAid());
        }
    }


    @Test
    public void getUserArticleTimeList(){

        String[] MONTH = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};

        // 查询出所有的信息并按照时间排序
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",1).orderByAsc("create_time");
        List<Article> articles = articleMapper.selectList(queryWrapper);

        // 创建treeMap并定义排序规则
        TreeMap<String, LinkedList<Article>> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                String[] split1 = s.split("-");
                String[] split2 = t1.split("-");
                if (Integer.valueOf(split1[0]).compareTo(Integer.valueOf(split2[0])) != 0){
                    return Integer.valueOf(split1[0]).compareTo(Integer.valueOf(split2[0]));
                }

                return Integer.valueOf(split1[1]).compareTo(Integer.valueOf(split2[1]));
            }
        });



        // 将内容按照月份归档
        for (Article article : articles){
            if (article.getStatus() == Article.PRIVETA_ARTICLE || article.getKind() != Article.USER_ARTICLE){
                continue;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(article.getCreateTime());
            String key = calendar.get(Calendar.YEAR)+"-"+MONTH[calendar.get(Calendar.MONTH)];
            if (treeMap.containsKey(key)){
                LinkedList<Article> articleList = treeMap.get(key);
                Article tmpArticle = new Article();
                tmpArticle.setUid(article.getUid());
                tmpArticle.setTitle(article.getTitle());
                tmpArticle.setViews(article.getViews());
                articleList.add(tmpArticle);
                treeMap.put(key, articleList);
            }else {
                LinkedList<Article> articleList = new LinkedList<>();
                Article tmpArticle = new Article();
                tmpArticle.setUid(article.getUid());
                tmpArticle.setTitle(article.getTitle());
                tmpArticle.setViews(article.getViews());
                articleList.add(tmpArticle);
                treeMap.put(key, articleList);
            }
        }



        for (String key : treeMap.keySet()){
            System.out.println(key+"  :  "+treeMap.get(key));
        }

    }


    @Test
    public  void getNew(){
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", 1).orderByDesc("create_time").last(" limit "+3);
        List<Article> articles = articleMapper.selectList(wrapper);
        for (Article article : articles){
            System.out.println(article);
        }
    }


}
