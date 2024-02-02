package com.atguigu.gulimail.member.exception;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/30 19:34
 */
public class UsernameExistException extends RuntimeException{
    public UsernameExistException() {
        super("用户名存在");
    }
}
