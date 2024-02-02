package com.atguigu.gulimail.search.feign;

import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/25 19:16
 */
@FeignClient("gulimail-product")
public interface ProductFeignService {
    @GetMapping("/product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);
}
