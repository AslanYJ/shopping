package com.solr.test;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {

    @Test
    public void testsolrCloud() {
        try{
            // 第一步：把solrJ相关的jar包添加到工程中。
            // 第二步：创建一个SolrServer对象，需要使用CloudSolrServer子类。构造方法的参数是zookeeper的地址列表。
            CloudSolrServer solrServer = new CloudSolrServer("192.168.208.40:2182,192.168.208.40:2183,192.168.208.40:2184");
            // 第三步：需要设置DefaultCollection属性。
            solrServer.setDefaultCollection("collection2");
            // 第四步：创建一SolrInputDocument对象。
            SolrInputDocument document = new SolrInputDocument();
            // 第五步：向文档对象中添加域
            document.addField("item_title", "测试商品");
            document.addField("item_price", "100");
            document.addField("id", "test001");
            // 第六步：把文档对象写入索引库。
            solrServer.add(document);
            // 第七步：提交。
            solrServer.commit();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test(){
        System.out.println("a" + "b");
    }
}
