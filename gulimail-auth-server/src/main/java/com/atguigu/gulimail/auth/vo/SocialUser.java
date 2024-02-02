package com.atguigu.gulimail.auth.vo;

import lombok.Data;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/31 11:49
 */
@Data
public class SocialUser {
    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;
}
