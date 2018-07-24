package com.solr.test;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestSolr {
    @Test
    public void TestAddSolr() throws IOException, SolrServerException {
        //创建一个SolrServer对象；SolrServer是一个接口，单机版的话用他的实现类HttpSolrServer
        //指定对应的URL
        SolrServer solrServer = new HttpSolrServer("http://192.168.208.40:8080/solr/collection1");
        //创建一个文档对象SolrInputDocument
        SolrInputDocument solrInputFields = new SolrInputDocument();
        //向文档对象中添加域的名称前提：必须在schema.xml中定义
        solrInputFields.addField("id","123");
        solrInputFields.addField("item_title","测试商品1");
        //把文档对象写入索引库
        solrServer.add(solrInputFields);
        //提交
        solrServer.commit();
    }
    @Test
    public void deleteSolr() throws Exception {
        //123
        SolrServer solrServer = new HttpSolrServer("http://192.168.208.40:8080/solr/collection1");
        //调用SolrServer对象的根据id删除的方法
        solrServer.deleteById("123");
        solrServer.commit();
    }
    @Test
    public void TestQuerySolr() throws Exception{
        //创建一个SolrServer的对象
        SolrServer solrServer = new HttpSolrServer("http://192.168.208.40:8080/solr/collection1");
        //创建一个SolrQuery对象
        SolrQuery solrQuery = new SolrQuery();
        //设置查询条件，过滤条件，分页条件，排序条件，高亮
        //设置查询条件
        solrQuery.setQuery("手机");
        //设置分页条件
        solrQuery.setStart(0);
        solrQuery.setRows(20);
        //设置默认搜索域
        solrQuery.set("df","item_keywords");
        //设置高亮
        solrQuery.setHighlight(true);
        //设置高亮的域
        solrQuery.addHighlightField("item_title");
        solrQuery.setHighlightSimplePre("<em>");
        solrQuery.setHighlightSimplePost("</em>");
        //执行查询
        QueryResponse response = solrServer.query(solrQuery);
        //取查询结果：总记录数，结果集
        SolrDocumentList solrDocumentList = response.getResults();
        //取查询结果总记录数
        System.out.println("查询结果总记录数："+solrDocumentList.getNumFound());
        for(SolrDocument document : solrDocumentList){
            System.out.println(document.getFieldValue("id"));
            //取高亮显示
            Map<String,Map<String,List<String>>> highlighting = response.getHighlighting();
            List<String> list = highlighting.get(document.getFieldValue("id")).get("item_title");
            String itemTitle = "";
            if(list != null && list.size() > 0){
                itemTitle = list.get(0);
            }else {
                itemTitle = (String)document.get("item_title");
            }
            System.out.println(itemTitle);
            System.out.println(document.get("item_sell_point"));
            System.out.println(document.get("item_price"));
            System.out.println(document.get("item_image"));
            System.out.println(document.get("item_category_name"));
            System.out.println("===============================================");
        }

    }
}
