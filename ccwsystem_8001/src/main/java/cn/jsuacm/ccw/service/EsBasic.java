package cn.jsuacm.ccw.service;

import cn.jsuacm.ccw.pojo.enity.ArticleEsEmpty;
import cn.jsuacm.ccw.pojo.enity.PageResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName EsBasic
 * @Description TODO
 * @Author h4795
 * @Date 2019/07/30 14:20
 */
@Component
public abstract class EsBasic<T> {


    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 高亮标签的前缀
     */
    public static final String PRE_TAG = "<em style='color:#dd4b39'>";

    /**
     *  高亮标签的后缀
     */
    public static final String POST_TAG = "</em>";



    /**
     * 高亮搜索
     * @param queryBuilder 查询的语句与条件
     * @param keys 查询的字段
     * @param current 查询的页面
     * @param pageSize 查询的数量
     * @return 返回一个包含分页信息的页面对象
     */
    public PageResult<T> search(QueryBuilder queryBuilder, List<String> keys, int current, int pageSize){
        PageResult<T> pageResult = new PageResult<>();
        try {
            // 构建查询
            NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
            searchQuery.withQuery(queryBuilder);

            // 高亮设置
            HighlightBuilder.Field[] fields = new HighlightBuilder.Field[keys.size()];
            for (int i = 0; i < keys.size(); i++) {
                fields[i] = new HighlightBuilder.Field(keys.get(i)).preTags(PRE_TAG)
                        .postTags(POST_TAG);
            }
            searchQuery.withHighlightFields(fields);

            // 分页设置
            searchQuery.withPageable(PageRequest.of(current, pageSize));
            NativeSearchQuery query = searchQuery.build();

            Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

            AggregatedPage<T> page = elasticsearchTemplate.queryForPage(query, entityClass, new SearchResultMapper() {

                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {

                    // 获取高亮搜索数据
                    List<T> list = new ArrayList<T>();
                    SearchHits hits = response.getHits();
                    SearchHit[] searchHits = hits.getHits();
                    for (SearchHit searchHit : searchHits) {
                        Class<T> entityClass = (Class<T>) getTClass();

                        // 公共字段
                        T data = (T) insertInfo(searchHit);

                        // 反射调用set方法将高亮内容设置进去
                        try {
                            for (String field : keys) {
                                HighlightField highlightField = searchHit.getHighlightFields().get(field);
                                if (highlightField != null) {
                                    String setMethodName = parSetName(field);
                                    Method setMethod = entityClass.getMethod(setMethodName, String.class);

                                    String highlightStr = highlightField.fragments()[0].toString();
                                    // 截取字符串
                                    if ("content".equals(field) && highlightStr.length() > 50) {

                                        /**
                                         *
                                         * 12*3/4*56/78*9/
                                         *  3 56 9
                                         *
                                         * 12     3/4         56/78      9/
                                         * 12   3   4         56  78        9
                                         *
                                         *
                                         */

                                        int length = 50;
                                        StringBuilder strHight = new StringBuilder();
                                        String[] preStrs = highlightStr.split(PRE_TAG);
                                        boolean preSign = false;
                                        boolean postSign = false;
                                        for (String pre : preStrs){
                                            if (preSign){
                                                strHight.append(PRE_TAG);
                                                postSign = true;
                                            }else {
                                                preSign = true;
                                            }
                                            String[] postStrs = pre.split(POST_TAG);
                                            for (String post : postStrs){
                                                strHight.append(post);
                                                length -= post.length();
                                                if (postSign){
                                                    postSign = false;
                                                    strHight.append(POST_TAG);
                                                }
                                            }
                                            if (length < 0){
                                                break;
                                            }
                                        }
                                        highlightStr = strHight.append("...").toString();

                                    }

                                    setMethod.invoke(data, highlightStr);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        list.add(data);
                    }

                    if (list.size() > 0) {
                        AggregatedPage<T> result = new AggregatedPageImpl<T>((List<T>) list, pageable,
                                response.getHits().getTotalHits());

                        return result;
                    }
                    return null;
                }
            });


            if (page == null){
                pageResult.setTatolSize(0L);
                pageResult.setPageContext(new ArrayList<T>());
            }else {

                pageResult.setPageContext(page.getContent());
                pageResult.setTatolSize(page.getTotalElements());
            }
            pageResult.setPageSize((long) pageSize);
            pageResult.setRow((long) current);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageResult;
    }

    /**
     * 通过一个field名称生成一个对象的set方法名称
     * @param fieldName field名称
     * @return 返回一个set方法名称
     */
    private String parSetName(String fieldName) {
        // 如果是空对象则返回空
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        // 确定开始大写的位置
        int startIndex = 0;
        if (fieldName.charAt(0) == '_') {
            startIndex = 1;
        }
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }



    public Class<T> getTClass()
    {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }



    public abstract T insertInfo(SearchHit searchHit);
}
