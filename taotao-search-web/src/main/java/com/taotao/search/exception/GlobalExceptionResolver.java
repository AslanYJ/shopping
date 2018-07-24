package com.taotao.search.exception;

import com.taotao.search.utils.SendMail;
import com.taotao.search.utils.StackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 */
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        logger.info("进入全局异常处理");
        logger.debug("测试handler的类型" + handler.getClass());
        //控制台打印异常
        e.printStackTrace();
        //向日志中写入日志
        logger.error("系统异常",e);
        //发送邮件
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    SendMail.sendMail("搜索系统出现异常,请及时处理", StackTrace.getStackTrace(e));
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //发送短信
        //显示错误界面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message","系统异常,请稍后操作");
        modelAndView.setViewName("error/exception");
        return modelAndView;
    }
}
