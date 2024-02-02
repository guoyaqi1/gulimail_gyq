package com.atguigu.gulimail.order.service;

import com.atguigu.gulimail.common.to.mq.SeckillOrderTo;
import com.atguigu.gulimail.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimail.common.utils.PageUtils;
import com.atguigu.gulimail.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:56:16
 */
public interface OrderService extends IService<OrderEntity> {

    /**
     * 获取当前订单的支付消息
     *
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 给订单返回确认页的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单确认
     * @param vo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity entity);

    String handlePayResult(PayAsyncVo vo);

    String asyncNotify(String notifyData);

    void createSeckillOrder(SeckillOrderTo seckillOrderTo);

    // void updateOrderStatus(String outTradeNo, Integer code);
}

