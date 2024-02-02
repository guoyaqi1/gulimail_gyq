package com.atguigu.gulimail.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:guoyaqi
 * @Date: 2023/10/23 21:26
 */
@Data
public class FareVo {
    private MemberAddressVo address;

    private BigDecimal fare;
}
