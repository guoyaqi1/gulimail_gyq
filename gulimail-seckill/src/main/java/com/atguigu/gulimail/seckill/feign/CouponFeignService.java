package com.atguigu.gulimail.seckill.feign;

import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/19 0:08
 */
@FeignClient("gulimail-coupon")
public interface CouponFeignService {

    /**
     * 查询最近三天需要参加秒杀商品的信息
     * @return
     */
    @GetMapping("/coupon/seckillsession/Lates3DaySession")
    R getLasts3DaySession();

}
