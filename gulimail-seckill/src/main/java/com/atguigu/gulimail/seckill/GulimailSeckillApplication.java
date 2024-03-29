package com.atguigu.gulimail.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
        //(exclude = {DataSourceAutoConfiguration.class})
public class GulimailSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimailSeckillApplication.class, args);
    }

}
