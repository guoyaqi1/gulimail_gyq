package com.atguigu.gulimail.auth.feign;

import com.atguigu.gulimail.auth.vo.SocialUser;
import com.atguigu.gulimail.auth.vo.UserLoginVo;
import com.atguigu.gulimail.auth.vo.UserRegistVo;
import com.atguigu.gulimail.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/30 20:40
 */
@FeignClient("gulimail-member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    R oauthlogin(SocialUser socialUser);
}

