package com.atguigu.gulimail.auth.feign;

import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/29 22:31
 */
@FeignClient("gulimail-thrid-part")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code")String code);

}
