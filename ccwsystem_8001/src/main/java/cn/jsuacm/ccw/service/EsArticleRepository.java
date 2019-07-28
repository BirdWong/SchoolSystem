package cn.jsuacm.ccw.service;

import cn.jsuacm.ccw.pojo.enity.ArticleEsEmpty;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @ClassName EsArticleService
 * @Description
 * @Author h4795
 * @Date 2019/07/24 17:12
 */
@Component
public interface EsArticleRepository extends ElasticsearchRepository<ArticleEsEmpty,Integer>{


}
