package com.taotao.search.controller;

import com.taotao.common.pojo.SearchItemResult;
import com.taotao.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearcController {
    private final static Integer rows = 60;//行数s
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SearchService searchService;

    @RequestMapping("/search")
    public String search(@RequestParam("q") String queryString,
                         @RequestParam(defaultValue = "1") Integer page,Model model) throws Exception{
        queryString =  new String(queryString.getBytes("iso8859-1"),"utf-8");
        SearchItemResult result = searchService.search(queryString,page,rows);
        //传递给界面
        model.addAttribute("query",queryString);
        model.addAttribute("totalPages",result.getPageTotal());
        model.addAttribute("itemList",result.getSearchList());
        model.addAttribute("page",page);
        //返回逻辑视图
        return "search";
    }
}
