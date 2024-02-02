package com.atguigu.gulimail.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/12 23:45
 * 封装订单提交的数据
 */
@Data
public class OrderSubmitVo {
    private Long addrId;//收货地址id
    private Integer payType;//支付方式

    private String orderToken;//防重令牌
    private BigDecimal payPrice;//应付价格
    private String note;//订单备注

    //用户相关信息 直接去session取出登录的用户


}
