package com.atguigu.gulimail.member.exception;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/30 19:34
 */
public class PhoneExsitException extends RuntimeException{
    public PhoneExsitException() {
        super("手机号存在");
    }
}
