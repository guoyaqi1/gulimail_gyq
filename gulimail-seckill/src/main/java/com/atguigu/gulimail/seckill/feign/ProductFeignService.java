package com.atguigu.gulimail.seckill.feign;

import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/20 19:23
 */
@FeignClient("gulimail-product")
public interface ProductFeignService {
    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

}
