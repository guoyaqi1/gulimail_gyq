package com.atguigu.gulimail.order.listener;

import com.atguigu.gulimail.common.to.mq.StockLockedTo;
import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.enume.OrderStatusEnum;
import com.atguigu.gulimail.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/5 19:54
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void handleStockLockedRelease(OrderEntity entity, Message msg, Channel channel) throws IOException {
        System.out.println("收到过期的订单消息，准备关闭订单"+entity.getOrderSn());
        try {
            orderService.closeOrder(entity);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    };

}
