package com.atguigu.gulimail.order.vo;

import javafx.beans.NamedArg;
import lombok.Data;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/16 22:39
 */
@Data
public class WareSkuLockVo {
    private String orderSn; //订单号

    private List<OrderItemVo> locks; //需要锁住的库存信息

}
