package com.atguigu.gulimail.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gulimail.order.config.AlipayTemplate;
import com.atguigu.gulimail.order.service.OrderService;
import com.atguigu.gulimail.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/10 15:01
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 1.将支付页让浏览器展示
     * 2.支付成功后 我们要跳到用户的订单列表项
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        //PayVo payVo = new PayVo();

        PayVo payVo =orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }
}
