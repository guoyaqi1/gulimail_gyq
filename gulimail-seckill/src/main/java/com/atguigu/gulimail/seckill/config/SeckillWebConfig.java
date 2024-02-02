package com.atguigu.gulimail.seckill.config;

import com.atguigu.gulimail.seckill.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/26 23:19
 */
@Configuration
public class SeckillWebConfig implements WebMvcConfigurer {

    @Resource
    LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");

    }
}
