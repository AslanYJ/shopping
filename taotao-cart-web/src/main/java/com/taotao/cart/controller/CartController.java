package com.taotao.cart.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import com.taotao.utils.CookieUtils;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *加入购物车Controller 我们已经有商品服务了，因此只需要写Controller
 */
@Controller
public class CartController {
    @Value("${CART_KEY}")
    private String CART_KEY;//cookiede的key
    @Value("${CART_EXPIER}")
    private Integer CART_EXPIER;//设置默认的过期时间，7天
    @Autowired
    private ItemService itemService;
    //展示商品
    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request) {
        //从cookie中获取到购物车列表
        List<TbItem> list = getTbItemList(request);
        //把购物车列表传递到jsp
        request.setAttribute("cartList",list);
        return "cart";
    }
    //新增商品
    @RequestMapping("/cart/add/{itemId}")
    public String addItemCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num,
                              HttpServletRequest request, HttpServletResponse response) {
        //1.从cookie中取出商品列表
        List<TbItem> list = getTbItemList(request);
        //2.判断商品中是否存在
        boolean isLive = false;
        for (TbItem item:
             list) {
            //3.如果存在则数量相加
            if(item.getId().longValue() == itemId.longValue()) {
                item.setNum(item.getNum() + num);
                isLive = true;
                break;
            }
        }
        //4.如果不存在就根据商品id查询商品信息，然后添加到商品列表中
        if(!isLive) {
            TbItem item = itemService.getItemById(itemId);
            String image = item.getImage();
            String[] images = image.split(",");
            image = images[0];
            item.setImage(image);
            item.setNum(num);
            list.add(item);
        }
        //5.把商品列表写入cookie
        CookieUtils.setCookie(request,response,CART_KEY,JsonUtils.objectToJson(list),CART_EXPIER,true);
        //6.返回值成功页面
        return "cartSuccess";
    }
    //删除商品
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCart(@PathVariable Long itemId, HttpServletRequest request,
                             HttpServletResponse response) {
        // 1、从url中取商品id
        // 2、从cookie中取购物车商品列表

        List<TbItem> list = getTbItemList(request);
        for (TbItem item:
             list) {
            // 4、删除商品。
            if(item.getId().longValue() == itemId.longValue()) {
                list.remove(item);
                break;
            }
        }
        //5.将修改后的结果存入cookie
        CookieUtils.setCookie(request,response,CART_KEY,JsonUtils.objectToJson(list),CART_EXPIER,true);
        //6.重定向到展示页面
        return "redirect:/cart/cart.html";
    }

    //更新商品数量
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public TaotaoResult updateNum(@PathVariable Long itemId, @PathVariable Integer num,
                                  HttpServletResponse response, HttpServletRequest request) {
        List<TbItem> list = getTbItemList(request);
        for (TbItem item:
             list) {
            if(item.getId().longValue() == itemId.longValue()) {
                item.setNum(num);
                break;
            }
        }
        //把结果写进cookie
        CookieUtils.setCookie(request,response,CART_KEY,JsonUtils.objectToJson(list),CART_EXPIER,true);

        return TaotaoResult.ok();
    }


    /**
     *
     * @param request
     * @return cookie的查询结果
     */
    private List<TbItem> getTbItemList(HttpServletRequest request) {
        String json = CookieUtils.getCookieValue(request,CART_KEY,true);
        //判断是否为空，空的话直接返回一个List（即没有保存cookie）
        if(StringUtils.isBlank(json)) {
            List<TbItem> list = new ArrayList<>();
            return list;
        }
        //不为空，将json转换成List然后返回
        List<TbItem> list = JsonUtils.jsonToList(json,TbItem.class);
        return list;
    }
}
