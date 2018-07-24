package com.taotao.order.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.utils.CookieUtils;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单的Controller
 */
@Controller
public class OrderController {
    @Value("${CART_KEY}")
    private String CART_KEY;
    @Autowired
    private OrderService orderService;
    @RequestMapping("/order/order-cart")
    public String showOrderCart(HttpServletRequest request) {
        //用户必须是登录状态
        //取用户ID
        TbUser user = (TbUser)request.getAttribute("user");
        System.out.println(user.getId());
        //根据用户ID取收获地址列表，这里就使用静态数据了
        //把收货地址列表取出传递给页面
        //从cookie中取购物车商品列表展示到页面
        List<TbItem> cartList = getCartItemList(request);
        request.setAttribute("cartList", cartList);
        //返回逻辑视图
        return "order-cart";

    }



    @RequestMapping(value="/order/create",method= RequestMethod.POST)
    public String createOrder(OrderInfo orderInfo, Model model,HttpServletRequest request) {
        // 1、接收表单提交的数据OrderInfo。
        // 2、补全用户信息。
        TbUser user = (TbUser)request.getAttribute("user");
        orderInfo.setUserId(user.getId());
        orderInfo.setBuyerNick(user.getUsername());
        // 3、调用Service创建订单。
        TaotaoResult result = orderService.createOrder(orderInfo);
        //设置逻辑视图中的内容
        model.addAttribute("orderId", result.getData().toString());
        model.addAttribute("payment", orderInfo.getPayment());
        //得到3天后的日期
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(3);
        model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
        //返回逻辑视图
        return "success";

    }


    private List<TbItem> getCartItemList(HttpServletRequest request){
        //从cookie中取购物车商品列表
        String json = CookieUtils.getCookieValue(request, CART_KEY, true);//为了防止乱码，统一下编码格式
        if(StringUtils.isBlank(json)){
            //说明cookie中没有商品列表，那么就返回一个空的列表
            return new ArrayList<TbItem>();
        }
        List<TbItem> list = JsonUtils.jsonToList(json,TbItem.class);
        return list;
    }



}
