package com.atguigu.gulimail.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/26 22:03
 */
@ToString
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
