package com.taotao.portal.controller;

import com.taotao.content.service.ContentService;
import com.taotao.pojo.AD1Node;
import com.taotao.pojo.TbContent;
import com.taotao.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PageController {
    @Value("${AD1_CATEGORY_ID}")
    private Long AD1_CATEGORY_ID;
    @Value("${AD1_WIDTH}")
    private Integer AD1_WIDTH;
    @Value("${AD1_WIDTH_B}")
    private Integer AD1_WIDTH_B;
    @Value("${AD1_HEIGHT}")
    private Integer AD1_HEIGHT;
    @Value("${AD1_HEIGHT_B}")
    private Integer AD1_HEIGHT_B;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ContentService contentService;
    @RequestMapping("/index")
    public String showIndex(Model model) {
        //单表查询内容
        List<TbContent> list = contentService.getContentListByCid(AD1_CATEGORY_ID);
        //将列表转换成AD1Node列表
        List<AD1Node> ad1NodeList = new ArrayList<>();
        for (TbContent content: list ) {
            AD1Node node = new AD1Node();
            node.setAlt(content.getTitle());
            node.setHeight(AD1_HEIGHT);
            node.setHeightB(AD1_HEIGHT_B);
            node.setSrc(content.getPic());
            node.setHref(content.getUrl());
            node.setWidth(AD1_WIDTH);
            node.setWidthB(AD1_WIDTH_B);
            node.setSrcB(content.getPic2());
            ad1NodeList.add(node);
        }
        //把列表转换成json数据
        String ad1 = JsonUtils.objectToJson(ad1NodeList);
        //把json数据传回界面
        model.addAttribute("ad1",ad1);
        return "index";
    }
}
