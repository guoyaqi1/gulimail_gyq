package com.atguigu.gulimail.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/26 22:04
 */
@ToString
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<String> attrValues;
}
