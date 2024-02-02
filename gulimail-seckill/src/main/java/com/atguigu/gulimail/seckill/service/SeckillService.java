package com.atguigu.gulimail.seckill.service;

import com.atguigu.gulimail.seckill.to.SeckillSkuRedisTo;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/18 23:50
 */
public interface SeckillService {
    @Scheduled
    public void uploadSeckillSkulatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num) throws InterruptedException;
}
