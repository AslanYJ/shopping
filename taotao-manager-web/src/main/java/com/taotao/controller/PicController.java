package com.taotao.controller;

import com.taotao.utils.FastDFSClient;
import com.taotao.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 图片上传Controller
 */
@Controller
public class PicController {
    //获得配置文件的值
    @Value("${IMAGE_SERVER_URL}")
    private  String IMAGE_SERVER_URL;
    @RequestMapping("/pic/upload")
    @ResponseBody
    public String fileUpload(MultipartFile uploadFile) {
        try {
            //获得文件的扩展名
            String originalFilename = uploadFile.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //创建一个FastDFS客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/client.conf");
            //执行上传处理,返回一个路径
            String path = fastDFSClient.uploadFile(uploadFile.getBytes(),extName);
            String url = IMAGE_SERVER_URL + path;
            Map result = new HashMap<>();
            result.put("error",0);
            result.put("url",url);
            String json = JsonUtils.objectToJson(result);
            return json;
        }catch (Exception e) {
            e.printStackTrace();
            Map result = new HashMap<>();
            result.put("error",1);
            result.put("message","上传图片失败");
            String json = JsonUtils.objectToJson(result);
            return json;
        }
    }
}
