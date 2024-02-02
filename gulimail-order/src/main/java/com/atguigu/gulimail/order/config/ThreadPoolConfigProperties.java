package com.atguigu.gulimail.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/27 0:55
 */
@ConfigurationProperties(prefix = "gulimail.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;
}
