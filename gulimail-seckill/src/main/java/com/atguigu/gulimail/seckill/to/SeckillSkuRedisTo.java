package com.atguigu.gulimail.seckill.to;

import com.atguigu.gulimail.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/20 1:22
 */
@Data
public class SeckillSkuRedisTo {
    @TableId
    private Long id;

    private String randomCode;
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private int seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    private SkuInfoVo skuInfo;

    private Long startTime;

    private Long endTime;
}
