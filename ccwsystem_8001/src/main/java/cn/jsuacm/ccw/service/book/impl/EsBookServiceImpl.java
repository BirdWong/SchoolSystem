package cn.jsuacm.ccw.service.book.impl;

import cn.jsuacm.ccw.mapper.book.BookMapper;
import cn.jsuacm.ccw.pojo.book.Book;
import cn.jsuacm.ccw.pojo.enity.BookEsEmpty;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import cn.jsuacm.ccw.service.EsBasic;
import cn.jsuacm.ccw.service.book.EsBookRepository;
import cn.jsuacm.ccw.service.book.EsBookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

/**
 * @ClassName EsBookServiceImpl
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 10:58
 */
@Service
public class EsBookServiceImpl extends EsBasic<BookEsEmpty> implements EsBookService {

    @Autowired
    private EsBookRepository repository;


    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 高亮标签的前缀
     */
    private static final String PRE_TAG = "<em style='color:#dd4b39'>";

    /**
     *  高亮标签的后缀
     */
    private static final String POST_TAG = "</em>";


    /**
     * 保存一个图书信息
     * @param book
     * @return
     */
    @Override
    public boolean saveBook(Book book){
        try {
            String bookStr = new ObjectMapper().writeValueAsString(book);
            BookEsEmpty bookEsEmpty = new ObjectMapper().readValue(bookStr, BookEsEmpty.class);
            repository.save(bookEsEmpty);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 更新剩余数量
     * @param id
     * @param status
     * @return
     */
    @Override
    public boolean changeUse(int id, int status){
        BookEsEmpty bookEsEmpty = repository.findById(id).get();
        if (bookEsEmpty == null){
            return  false;
        }
        bookEsEmpty.setHasUse(bookEsEmpty.getHasUse() + status);
        repository.save(bookEsEmpty);
        return true;
    }



    /**
     * 通过id删除一个es文档
     *
     * @param id 文章的id ， 同时也是es的标识id
     * @return
     */
    @Override
    public boolean deleteById(int id) {
        try {
            repository.deleteById(id);
            return true;
        }catch (Exception e) {
            return false;
        }
    }



    /**
     * 通过关键字搜索书名
     * @param keyWord 关键字
     * @param current 当前页
     * @param pageSize 页面大小
     * @return
     */
    @Override
    public PageResult<BookEsEmpty> searchByName(String keyWord, int current, int pageSize){
        MatchQueryBuilder titleQuery = QueryBuilders.matchQuery("title", keyWord);
        ArrayList<String> keys = new ArrayList<>();
        keys.add("title");
        return search(titleQuery, keys, current, pageSize);
    }

    /**
     * 通过书籍名称， 作者名， 出版社名称查找书籍
     *
     * @param keyWord
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<BookEsEmpty> searchByMore(String keyWord, int current, int pageSize) {
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().
                must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("title",keyWord ).boost(3))
                        .should(QueryBuilders.matchQuery("author",keyWord).boost(2))
                        .should(QueryBuilders.matchQuery("publisher", keyWord).boost(1))
                );
        ArrayList<String> keys = new ArrayList<>();
        keys.add("title");
        keys.add("author");
        keys.add("publisher");

        return search(queryBuilder, keys, current, pageSize);
    }


    /**
     * 将搜索的数据转换成实体
     * @param searchHit
     * @return
     */
    @Override
    public BookEsEmpty insertInfo(SearchHit searchHit) {
        BookEsEmpty data = new BookEsEmpty();
        data.setId(Integer.valueOf(String.valueOf(searchHit.getSourceAsMap().get("id"))));
        data.setHasUse(Integer.valueOf(String.valueOf(searchHit.getSourceAsMap().get("hasUse"))));
        data.setAuthor(String.valueOf(searchHit.getSourceAsMap().get("author")));
        data.setIsbn(String.valueOf(searchHit.getSourceAsMap().get("isbn")));
        data.setPages(Integer.valueOf(String.valueOf(searchHit.getSourceAsMap().get("pages"))));
        data.setPrice(String.valueOf(searchHit.getSourceAsMap().get("price")));
        data.setPublisher(String.valueOf(searchHit.getSourceAsMap().get("publisher")));
        data.setSize(Integer.valueOf(String.valueOf(searchHit.getSourceAsMap().get("size"))));
        data.setTitle(String.valueOf(searchHit.getSourceAsMap().get("title")));
        Object pubdate = searchHit.getSourceAsMap().get("pubdate");
        if (pubdate != null){
            data.setPubdate(new Date(Long.valueOf(pubdate.toString())));
        }
        return data;
    }
}
