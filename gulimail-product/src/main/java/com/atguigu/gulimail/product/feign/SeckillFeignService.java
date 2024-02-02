package com.atguigu.gulimail.product.feign;

import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.product.fallbace.SeckillServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/26 0:01
 */
@FeignClient(value="gulimail-seckill",fallback = SeckillServiceFallback.class)
public interface SeckillFeignService {
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);


}
