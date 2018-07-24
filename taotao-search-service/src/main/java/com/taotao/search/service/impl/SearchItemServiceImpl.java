package com.taotao.search.service.impl;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SearchItemServiceImpl implements SearchItemService {
    //注入SolrServer在bean中装配
    @Autowired
    private SolrServer solrServer;
    @Autowired
    private SearchItemMapper searchItemMapper;
    @Override
    public TaotaoResult addSerchItem() throws Exception {
        //查询所有数据
        List<SearchItem> searchItemList = searchItemMapper.getItemList();
        //遍历商品数据，添加到索引库
        for (SearchItem searchItem:
            searchItemList ) {
            //为每个商品创建文档对象SolrInputDocument
            SolrInputDocument document = new SolrInputDocument();
            //对文档对象添加域
            document.addField("id", searchItem.getId());
            document.addField("item_title", searchItem.getTitle());
            document.addField("item_sell_point", searchItem.getSell_point());
            document.addField("item_price", searchItem.getPrice());
            document.addField("item_image", searchItem.getImage());
            document.addField("item_category_name", searchItem.getCategory_name());
            document.addField("item_desc", searchItem.getItem_desc());
            //向索引库中添加文档
            solrServer.add(document);
        }
        //提交修改
        solrServer.commit();
        //返回结果
        return TaotaoResult.ok();
    }

    public TaotaoResult addDocument(Long itemId) throws Exception {
        // 1、根据商品id查询商品信息。
        SearchItem searchItem = searchItemMapper.getItemById(itemId);
        // 2、创建一SolrInputDocument对象。
        SolrInputDocument document = new SolrInputDocument();
        // 3、使用SolrServer对象写入索引库。
        document.addField("id", searchItem.getId());
        document.addField("item_title", searchItem.getTitle());
        document.addField("item_sell_point", searchItem.getSell_point());
        document.addField("item_price", searchItem.getPrice());
        document.addField("item_image", searchItem.getImage());
        document.addField("item_category_name", searchItem.getCategory_name());
        document.addField("item_desc", searchItem.getItem_desc());
        // 5、向索引库中添加文档。
        solrServer.add(document);
        solrServer.commit();
        // 4、返回成功，返回TaotaoResult。
        return TaotaoResult.ok();
    }


}
