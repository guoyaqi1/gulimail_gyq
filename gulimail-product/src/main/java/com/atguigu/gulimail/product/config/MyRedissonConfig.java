package com.atguigu.gulimail.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/22 14:17
 */
@Configuration
public class MyRedissonConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException{
        //1.创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.80.128:6379");

        //2.根据Config 创建出RedissonClient的实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
