package com.atguigu.gulimail.common.to.mq;

import lombok.Data;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/3 22:01
 */
@Data
public class StockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 库存id
     */
    private Long wareId;
    /**
     * 锁定状态
     */
    private Integer lockStatus;
}
