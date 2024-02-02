package com.atguigu.gulimail.product.fallbace;

import com.atguigu.gulimail.common.exception.BizCodeEnume;
import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author:guoyaqi
 * @Date: 2024/1/2 9:28
 */
@Slf4j
public class SeckillServiceFallback implements SeckillFeignService {

    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("熔断方法调用...getSkuSeckillInfo");
        return R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(),BizCodeEnume.TO_MANY_REQUEST.getMsg());

    }
}
