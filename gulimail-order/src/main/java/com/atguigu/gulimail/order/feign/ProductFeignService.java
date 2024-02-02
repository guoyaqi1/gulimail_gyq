package com.atguigu.gulimail.order.feign;

import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/14 0:13
 */
@FeignClient("gulimail-product")
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id") Long skuId);
}
