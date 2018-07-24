package com.taotao.item.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HtmlGenController {
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @RequestMapping("genhtml")
    public String genhtml() throws Exception {
        //生产静态页面
        Configuration configuration = freeMarkerConfig.getConfiguration();
        Template template = configuration.getTemplate("hello.ftl");
        Map data = new HashMap<>();
        data.put("hello", "spring freemarker test");
        Writer out = new FileWriter(new File("E:/freemarker/out/test.html"));
        template.process(data,out);
        out.close();
        return "OK";
    }
}
