package cn.jsuacm.ccw.service.book;

import cn.jsuacm.ccw.pojo.enity.BookEsEmpty;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @ClassName EsBookRepository
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 10:57
 */
public interface EsBookRepository extends ElasticsearchRepository<BookEsEmpty, Integer>{
}
