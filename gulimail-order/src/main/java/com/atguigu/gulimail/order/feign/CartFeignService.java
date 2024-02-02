package com.atguigu.gulimail.order.feign;

import com.atguigu.gulimail.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/10/14 22:06
 */
@FeignClient("gulimail-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();

}
