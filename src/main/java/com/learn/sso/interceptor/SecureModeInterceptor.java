package com.learn.sso.interceptor;

import com.learn.sso.common.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用于安全模式拦截判断的拦截器
 */
public class SecureModeInterceptor implements HandlerInterceptor {

    @Autowired
    private Config config;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        // 处在安全模式, 并且请求是安全的情况下才放行
        boolean ret = config.isSecureMode() || request.isSecure();
        if (!ret) {
            response.getWriter().write("must https");
        }
        return ret;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // do nothing
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
