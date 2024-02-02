package com.atguigu.gulimail.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author:guoyaqi
 * @Date: 2023/10/14 17:03
 */
//@Data
public class OrderConfirmVo {

    //收货地址
    @Setter @Getter
    List<MemberAddressVO> address;

    //所有选中的购物项
    @Setter @Getter
    List<OrderItemVo> items;

    //发票记录

    //优惠券信息
    @Setter @Getter
    Integer integration;

    @Setter @Getter
    Map<Long, Boolean> stocks;

    //订单总额

    BigDecimal total;

    public BigDecimal getTotal() {
        BigDecimal sum=new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum=sum.add(multiply);
            }
        }

        return sum;
    }
    //防重令牌 防止重复提交
    @Setter @Getter
    String orderToken;

    public Integer getCount(){
        Integer i=0;
        if(items!=null){
            for(OrderItemVo item:items){
                i+=item.getCount();
            }
        }
        return i;
    }

    //应付价格
//    BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }

//    public void setStocks(Map<Long, Boolean> map) {
//    }
}
