package com.atguigu.gulimail.thridpart.controller;

import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.thridpart.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/29 22:15
 */

@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 提供给别的服务进行调用
     * @param phone
     * @param code
     * @return
     */
    @GetMapping
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code")String code){
        smsComponent.sendCode(phone,code);
        return R.ok();
    }
}
