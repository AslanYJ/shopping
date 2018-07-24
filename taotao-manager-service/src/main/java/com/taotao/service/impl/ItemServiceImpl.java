package com.taotao.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import com.taotao.service.jedis.JedisClient;
import com.taotao.utils.IDUtils;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;

/**
 * 商品管理
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "itemAddTopic")
    private Destination itemAddTopic;
    @Autowired
    private JedisClient jedisClient;
    @Value("${ITEM_INFO}")
    private String ITEM_INFO;//商品的前缀
    @Value("${ITEM_EXPIRE}")
    private Integer ITEM_EXPIRE;//商品的后缀
    @Override
    public TbItem getItemById(long itemId) {
        //查询缓存
        try{
            String json = jedisClient.get(ITEM_INFO + ":"+ itemId + ":BASE");
            if (StringUtils.isNotBlank(json)) {
                TbItem item = JsonUtils.jsonToPojo(json,TbItem.class);
                return  item;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        //如果在缓存中没有就添加进缓存
        try{
            //添加缓存
            jedisClient.set(ITEM_INFO + ":"+ itemId + ":BASE", JsonUtils.objectToJson(item));
            //设置过期时间
            jedisClient.expire(ITEM_INFO + ":"+ itemId + ":BASE",ITEM_EXPIRE);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public TbItemDesc getDescById(long itemId) {
        //查询缓存
        try{
            String json = jedisClient.get(ITEM_INFO + ":" + itemId  + ":DESC");
            if(StringUtils.isNotBlank(json)) {
                TbItemDesc desc = JsonUtils.jsonToPojo(json,TbItemDesc.class);
                return desc;
            }
        }catch(Exception e) {

        }
        TbItemDesc desc = itemDescMapper.selectByPrimaryKey(itemId);
        //如果在缓存中没有就添加进缓存
        try{
            jedisClient.set(ITEM_INFO + ":" + itemId  + ":DESC",JsonUtils.objectToJson(desc));
            jedisClient.expire(ITEM_INFO + ":" + itemId  + ":DESC",ITEM_EXPIRE);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return desc;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        //1.在执行查询之前配置分页条件。使用PageHelper的静态方法
        PageHelper.startPage(page,rows);
        //2.执行查询
        TbItemExample tbItemExample = new TbItemExample();
        List<TbItem> list = tbItemMapper.selectByExample(tbItemExample);
        //3.创建PageInfo对象
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        //设置数目
        result.setTotal(pageInfo.getTotal());
        //设置返回的数据
        result.setRows(list);
        return result;
    }

    @Override
    public TaotaoResult addItem(TbItem item, String desc) {
        final long id = IDUtils.genItemId();
        item.setId(id);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        item.setStatus((byte) 1);
        tbItemMapper.insert(item);
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        itemDescMapper.insert(itemDesc);
        //使用ActiveMq发送消息
        jmsTemplate.send(itemAddTopic,new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(id + "");
                return textMessage;
            }
        });
        return TaotaoResult.ok();
    }


    @Override
    public TbItem updateItem(long itemId) {

        return null;
    }
}
