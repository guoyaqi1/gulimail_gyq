package com.atguigu.gulimail.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/18 19:59
 */
@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class HelloSchedule {
    @Async
    @Scheduled(cron="* * * * * ?")
    public void hello() throws InterruptedException {
        log.info("hello");
        Thread.sleep(1000);
    }
}
