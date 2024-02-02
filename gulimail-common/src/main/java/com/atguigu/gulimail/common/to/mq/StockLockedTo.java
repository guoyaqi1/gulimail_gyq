package com.atguigu.gulimail.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/3 21:45
 */
@Data
public class StockLockedTo {
    /**
     * 库存工作单的id
     */
    private Long id;
    /**
     * 工作详情的所有id
     */
    private StockDetailTo detail;
}
