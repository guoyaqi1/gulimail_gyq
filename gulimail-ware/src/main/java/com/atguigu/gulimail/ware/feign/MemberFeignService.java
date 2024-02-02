package com.atguigu.gulimail.ware.feign;

import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author:guoyaqi
 * @Date: 2023/10/23 20:59
 */
@FeignClient("gulimail-member")
public interface MemberFeignService {
    /**
     * 根据id获取用户地址信息
     * @param id
     * @return
     */
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);

}
