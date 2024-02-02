package com.atguigu.gulimail.seckill.interceptor;

import com.atguigu.gulimail.common.constant.AuthServerConstant;
import com.atguigu.gulimail.common.vo.MemberRespVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/10 15:45
 */

public class LoginUserInterceptor implements HandlerInterceptor {

    //ThreadLocal共享数据
    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/kill",uri);
        if (match){
            return true;
        }


        if(match){
            MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
           if (attribute!=null){
               loginUser.set(attribute);
               return true;
           }else{
               //没登录就去登录
               request.getSession().setAttribute("msg","请先登录");
               response.sendRedirect("http://auth.gulimail.com/login.html");
               return false;
           }
        }
        return true;
        
    }
}

