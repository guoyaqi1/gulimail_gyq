package com.atguigu.gulimail.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author:guoyaqi
 * @Date: 2023/9/1 20:30
 */
@Data
@ToString
public class UserInfoTo {
    private Long userId;
    private String userKey;

    /**
     * 是否临时用户
     */
    private Boolean tempUser = false;
}
