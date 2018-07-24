package com.taotao.order.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {
    @Value("${ORDER_ID_GEN_KEY}")
    private String ORDER_ID_GEN_KEY;//订单生成key
    @Value("${ORDER_ID_BEGIN_VALUE}")
    private String ORDER_ID_BEGIN_VALUE; //订单初始值
    @Value("${ORDER_ITEM_ID_GEN_KEY}")
    private String ORDER_ITEM_ID_GEN_KEY; //订单明细表主键生成key
    @Autowired
    private TbOrderMapper tbOrderMapper;
    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    @Autowired
    private JedisClient jedisClient;
    @Override
    public TaotaoResult createOrder(OrderInfo orderInfo) {
        //生成订单号，可以使用redis的incr方法生成
        if(!jedisClient.exists(ORDER_ID_GEN_KEY)) {
            //设置订单号
            jedisClient.set(ORDER_ID_GEN_KEY,ORDER_ID_BEGIN_VALUE);
        }
        String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
        //需要补全pojo的属性，其它的都是从页面传递过来的
        orderInfo.setOrderId(orderId);
        //付款状态，1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭，刚开始肯定是未付款
        orderInfo.setStatus(1);
        //订单创建时间
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        //向订单表插入数据，由于OrderInfo继承自TbOrder，因此这里才可以直接把orderInfo作为参数
        tbOrderMapper.insert(orderInfo);

        //向订单明细表插入数据
        List<TbOrderItem> orderItems  = orderInfo.getOrderItems();
        for (TbOrderItem tbOrderItem:
             orderItems) {
            //获得明细主键，第一次使用ORDER_ITEM_ID_GEN_KEY这个key，是没有初始值的，那么会自动将初始值变为1
            String oid = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY).toString();
            //这里之所以只补充了两个属性，是因为tbOrderItem自身已经有itemId了。
            tbOrderItem.setId(oid);//这是订单明细表的主键
            tbOrderItem.setOrderId(orderId);
            //插入明细数据
            tbOrderItemMapper.insert(tbOrderItem);
        }

        //向订单物流表插入数据
        TbOrderShipping tbOrderShipping = orderInfo.getOrderShipping();
        tbOrderShipping.setOrderId(orderId);
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setUpdated(new Date());
        tbOrderShippingMapper.insert(tbOrderShipping);
        //返回订单号
        return TaotaoResult.ok(orderId);
    }
}
