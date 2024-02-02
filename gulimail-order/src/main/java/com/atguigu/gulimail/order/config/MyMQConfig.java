package com.atguigu.gulimail.order.config;

import com.atguigu.gulimail.order.entity.OrderEntity;
import com.rabbitmq.client.AMQP;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:guoyaqi
 * @Date: 2023/11/21 23:29
 */
@Configuration
public class MyMQConfig {

    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单消息：准备关闭订单"+entity.getOrderSn());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @Bean
    public Queue orderDelayQueue(){

        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-tt10",6000);

        Queue queue = new Queue("order.delay.queue",true,false,false);
        return queue;

    }

    @Bean
    public Queue orderReleaseOrderQueue(){
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-tt10",6000);

        Queue queue = new Queue("order.release.order.queue",true,false,false);
        return queue;
    }

    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange",true,false);

    }

    @Bean
    public Binding orderCreateOrderBinding(){
        return new Binding("order-delay-queue"
                 ,Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);

    }

    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding("order-release-queue"
                ,Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);

    }

    /**
     * 订单释放直接和库存释放进行绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding("stock.release.stock.queue"
                ,Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);

    }

    public Binding orderSeckillOrderQueueBingding(){
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);

    }
}
