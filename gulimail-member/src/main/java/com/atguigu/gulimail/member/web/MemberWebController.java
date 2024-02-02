package com.atguigu.gulimail.member.web;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.member.feign.OrderFeignService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/10 15:43
 */
@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, Model model){

        Map<String,Object> page = new HashMap<>();
        page.put("page",pageNum);

        //查出当前登录的用户的所有订单列表数据
        R r = orderFeignService.listWithItem(page);
        System.out.println(JSON.toJSON(r));
        model.addAttribute("orders",r);
        return "orderList";

    }

}
