package com.taotao.item.listener;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ItemAddMessageListener implements MessageListener {
    @Autowired
    private ItemService itemService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${HTML_OUT_PATH}")
    private String HTML_OUT_PATH;//保存的路径
    @Override
    public void onMessage(Message message){

        try {
            TextMessage textMessage = (TextMessage) message;
            String itemIdStr = textMessage.getText();
            Long itemId = Long.parseLong(itemIdStr);
            Thread.sleep(1000);
            TbItem tbItem = itemService.getItemById(itemId);
            TbItemDesc itemDesc = itemService.getDescById(itemId);
            Item item = new Item(tbItem);
            //使用freemarker生成静态页面
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //创建模板并加载模板对象
            Template template = configuration.getTemplate("item.ftl");
            //准备模板需要的数据
            Map data = new HashMap<>();
            data.put("item", item);
            data.put("itemDesc", itemDesc);
            //指定输出的目录及文件名
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(HTML_OUT_PATH+itemIdStr+".html")),"UTF-8");
            //生成静态页面
            template.process(data, out);
            //关闭流
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
