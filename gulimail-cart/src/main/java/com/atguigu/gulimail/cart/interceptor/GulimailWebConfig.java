package com.atguigu.gulimail.cart.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author:guoyaqi
 * @Date: 2023/9/1 20:40
 */
@Configuration
public class GulimailWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {//注册拦截器
        registry.addInterceptor(new CartInterceptor())
                .addPathPatterns("/**");
    }
}
