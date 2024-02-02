package com.atguigu.gulimail.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimail.auth.feign.MemberFeignService;
import com.atguigu.gulimail.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimail.auth.vo.UserLoginVo;
import com.atguigu.gulimail.auth.vo.UserRegistVo;
import com.atguigu.gulimail.common.constant.AuthServerConstant;
import com.atguigu.gulimail.common.exception.BizCodeEnume;
import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.common.vo.MemberRespVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author:guoyaqi
 * @Date: 2023/8/29 21:14
 */
@Controller
public class LoginController {
   @Autowired
    ThirdPartFeignService thirdPartFeignService;

   @Resource
   StringRedisTemplate redisTemplate;

   @Autowired
    MemberFeignService memberFeignService;


   @ResponseBody
   @GetMapping("/sms/sendcode")
   public R sendCode(@RequestParam("phone") String phone){

       //1.接口防刷
       String redisCode =redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone);
       long l = Long.parseLong(redisCode.split("_")[1]);
       if(System.currentTimeMillis()-1<60000){
           //60秒内不能重发
           return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
       }


               //2.验证码的再次校验 redis
       String code = UUID.randomUUID().toString().substring(0,5);
       String subString = code+"_"+System.currentTimeMillis();

       redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,subString,10, TimeUnit.MINUTES);

       thirdPartFeignService.sendCode(phone,code);
       return R.ok();
   }

   //注册成功回到登录页
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result,
                         RedirectAttributes redirectattributes){
        //如果有错误回到注册页面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectattributes.addFlashAttribute("errors",errors);

            //效验出错回到注册页面
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //1.校验验证码
        String code = vo.getCode();

        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX+vo.getPhone());
        if(!StringUtils.isEmpty(s)){
            if(code.equals(s.split("_")[0])){
                //删除验证码
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX+vo.getPhone());
                //验证码通过  真正注册
                R r = memberFeignService.regist(vo);
                if(r.getCode()==0){
                    //成功
                    return "redirect:http://localhost:20000/login.html";
                }else {
                    //失败
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectattributes.addFlashAttribute("errors",errors);
                    return "redirect:http://localhost:20000/reg.html";
                }

            }else{
                //效验出错回到注册页面
                Map<String,String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectattributes.addFlashAttribute("errors",errors);
                //校验出错 转发到注册页
                return "redirect:http://localhost:20000/reg.html";
            }

        }else{
            //效验出错回到注册页面
            Map<String,String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            redirectattributes.addFlashAttribute("errors",errors);
            //校验出错 转发到注册页
            return "redirect:http://localhost:20000/reg.html";
        }

        //注册成功 回到登录页




    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectattributes, HttpSession session){
       //远程登录
        R login = memberFeignService.login(vo);
        if (login.getCode()==0){
            MemberRespVo data = login.getData("data",new TypeReference<MemberRespVo>(){

            });
            //成功
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect://http://localhost:10000";
        }else{
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectattributes.addFlashAttribute("errors",errors);
            return "redirect:http://localhost:20000";
        }

    }


}
