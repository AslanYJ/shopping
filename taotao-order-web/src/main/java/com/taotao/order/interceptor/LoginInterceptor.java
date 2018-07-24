package com.taotao.order.interceptor;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;
import com.taotao.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Value("${TOKEN_KEY}")
    private String TOKEN_KEY;
    @Value("${SSO_URL}")
    private String SSO_URL;
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //执行Handler之前执行此方法，拦截请求让用户登录就在这个方法拦截
        //1.从cookie中取token。
        String token = CookieUtils.getCookieValue(httpServletRequest,TOKEN_KEY);
        //2.没有token，需要跳转到登录页面。
        if(StringUtils.isBlank(token)) {
            //取当前请求的url
            String url = httpServletRequest.getRequestURL().toString();
            //跳转到登录页面，用redirect比较合适，登录之后还要回到当前页面，因此要在请求url中添加一个回调地址
            httpServletResponse.sendRedirect(SSO_URL + "/page/login?url=" + url);
            //由于没有登录，拦截
            return false;
        }
        //3.有token。调用sso系统的服务，根据token查询用户信息。
        TaotaoResult taotaoResult = userService.getUserMessageByToken(token);
        //4.如果查不到用户信息。用户登录已经过期。需要跳转到登录页面。
        if(taotaoResult.getStatus() != 200) {
            //取当前请求的url
            String url = httpServletRequest.getRequestURL().toString();
            //跳转到登录页面，用redirect比较合适，登录之后还要回到当前页面，因此要在请求url中添加一个回调地址
            httpServletResponse.sendRedirect(SSO_URL + "/page/login?url=" + url);
            //由于没有登录，拦截
            return false;
        }
        TbUser user = (TbUser) taotaoResult.getData();
        httpServletRequest.setAttribute("user",user);
        //5.查询到用户信息。放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        // handler执行之后，modelAndView返回之前，可以对返回值进行处理
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        // 在ModelAndView返回之后，这时只能做些异常处理了
    }
}
