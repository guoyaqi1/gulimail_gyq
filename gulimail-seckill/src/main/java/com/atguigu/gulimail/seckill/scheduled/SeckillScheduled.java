package com.atguigu.gulimail.seckill.scheduled;

import com.atguigu.gulimail.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/18 23:13
 */
@Slf4j
@Service
public class  SeckillScheduled {
    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";


    @Scheduled(cron="0 0 3 * * ?")
    public void uploadSeckillSkulatest3Days(){
        //1.重复上架无需处理
        log.info("上架秒杀的商品信息....");
        //分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.MINUTES);
        try{
            seckillService.uploadSeckillSkulatest3Days();
        }finally {
            lock.unlock();
        }
    }
}
