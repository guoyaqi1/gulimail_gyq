package com.atguigu.gulimail.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/27 23:52
 */
@Data
public class SeckillOrderTo {
    private String orderSn;
    private Long promotionSessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer num;
    private Long memberId;
}
