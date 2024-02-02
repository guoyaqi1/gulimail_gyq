package com.atguigu.gulimail.member.feign;

import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/12 0:08
 */
@FeignClient("gulimail-member")
public interface OrderFeignService {

    @GetMapping("/order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);
}
