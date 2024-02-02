package com.atguigu.gulimail.order.feign;

import com.atguigu.gulimail.order.vo.MemberAddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author:guoyaqi
 * @Date: 2023/10/14 21:15
 */
@FeignClient("gulimail-member")
public interface MemberFeignService {

//    @Autowired
//    MemberReceiveAddressService memberReceiveAddressService;

    @GetMapping("/{memberId}/addresses")
    List<MemberAddressVO> getAddress(@PathVariable("memberId") Long memberId);
}
