package com.atguigu.gulimail.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/10/14 17:10
 */
@Data
public class OrderItemVo {
    //购物车选项
    private Long skuId;

    private Boolean check = true;

    private String title;

    private String image;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;

    //TODO 查询库存状态
    private Boolean hasStock;

    private BigDecimal weight;

}
