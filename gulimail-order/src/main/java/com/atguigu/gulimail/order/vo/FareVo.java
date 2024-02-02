package com.atguigu.gulimail.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/13 22:33
 */
@Data
public class FareVo {
    private MemberAddressVO address;
    private BigDecimal fare;
}
