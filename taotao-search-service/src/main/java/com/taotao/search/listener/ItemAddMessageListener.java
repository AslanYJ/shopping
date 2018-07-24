package com.taotao.search.listener;

import com.taotao.search.service.SearchItemService;
import com.taotao.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ItemAddMessageListener implements MessageListener {
    @Autowired
    private SearchItemService searchItemService;
    @Override
    public void onMessage(Message message) {
        try{
            //从消息中获取商品的id
            TextMessage textMessage = (TextMessage)message;
            String text = textMessage.getText();
            Long itemId = Long.parseLong(text);
            //根据商品ID查询数据，添加商品到索引库，因为事务提交需要一段时间，为了避免查询不到商品的情况出现
            //所以需要设置一下等待的时间
            //等待事务的提交
            Thread.sleep(1000);
            //查询商品，并将商品添加到索引库
            searchItemService.addDocument(itemId);
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}
