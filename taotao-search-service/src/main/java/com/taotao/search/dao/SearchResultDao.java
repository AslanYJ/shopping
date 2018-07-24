package com.taotao.search.dao;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchItemResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dao层负责获取数据
 */
@Repository
public class SearchResultDao {
    @Autowired
    private SolrServer solrServer;

    public SearchItemResult search(SolrQuery query)throws Exception {
        //根据query对象进行查询
        QueryResponse response = solrServer.query(query);
        //取查询结果(商品列表)
        SolrDocumentList solrDocumentList = response.getResults();
        List<SearchItem> searchItemList = new ArrayList<>();
        for (SolrDocument solrDocument:solrDocumentList) {
            SearchItem item  = new SearchItem();
            item.setId((String) solrDocument.get("id"));
            item.setCategory_name((String) solrDocument.get("item_category_name"));
            String images = (String) solrDocument.get("item_image");
            if(StringUtils.isNotBlank(images)) {
                images = images.split(",")[0];
            }
            item.setImage(images);
            item.setPrice((long) solrDocument.get("item_price"));
            item.setSell_point((String) solrDocument.get("item_sell_point"));
            //取高亮显示
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            String itemTitle = "";
            //有高亮显示的内容时。
            if (list != null && list.size() > 0) {
                itemTitle = list.get(0);
            } else {
                itemTitle = (String) solrDocument.get("item_title");
            }
            item.setTitle(itemTitle);
            //添加到商品列表
            searchItemList.add(item);

        }
        SearchItemResult searchItemResult = new SearchItemResult();
        //取总记录数
        searchItemResult.setRecordCount(solrDocumentList.getNumFound());
        searchItemResult.setSearchList(searchItemList);
        return searchItemResult;
    }

}
