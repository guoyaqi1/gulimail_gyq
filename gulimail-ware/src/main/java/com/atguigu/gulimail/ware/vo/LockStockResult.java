package com.atguigu.gulimail.ware.vo;

import lombok.Data;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/16 22:52
 */
@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}
