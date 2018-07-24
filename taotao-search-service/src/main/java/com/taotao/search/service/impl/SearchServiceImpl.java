package com.taotao.search.service.impl;

import com.taotao.common.pojo.SearchItemResult;
import com.taotao.search.dao.SearchResultDao;
import com.taotao.search.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchResultDao searchResultDao;

    public SearchItemResult search(String queryString, Integer page, Integer rows) throws Exception {
        //创建一个SolrQuery对象
        SolrQuery solrQuery = new SolrQuery();
        //设置查询条件
        solrQuery.setQuery(queryString);
        //设置分页条件
        solrQuery.setStart((page - 1) * rows);
        solrQuery.setRows(rows);
        //指定默认的搜索域
        solrQuery.set("df", "item_title");
        //设置高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("item_title");
        solrQuery.setHighlightSimplePre("<em style=\"color:red\">");
        solrQuery.setHighlightSimplePost("</em>");
        //执行查询，调用searchResultDao得到SearchResult
        SearchItemResult searchItemResult = searchResultDao.search(solrQuery);
        //需要计算总页数
        long pageTotal = searchItemResult.getRecordCount()/rows;
        if(searchItemResult.getRecordCount() % rows > 0) {
            pageTotal++;
        }
        searchItemResult.setPageTotal(pageTotal);
        //返回SearchResult
        return searchItemResult;
    }
}
