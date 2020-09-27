package com.learn.sso.common;

import com.learn.sso.interceptor.SecureModeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor getSecureModeInterceptor() {
        return new SecureModeInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册SecureModeInterceptor拦截器
         registry.addInterceptor(getSecureModeInterceptor()).addPathPatterns("/*");
    }
}
