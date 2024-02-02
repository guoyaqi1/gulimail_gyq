package com.atguigu.gulimail.order.service.impl;

import com.atguigu.gulimail.order.entity.OrderEntity;
import com.atguigu.gulimail.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimail.common.utils.PageUtils;
import com.atguigu.gulimail.common.utils.Query;

import com.atguigu.gulimail.order.dao.OrderItemDao;
import com.atguigu.gulimail.order.entity.OrderItemEntity;
import com.atguigu.gulimail.order.service.OrderItemService;



@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    @RabbitListener(queues = {"hello-java-queue"})
    public void recieveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel) throws InterruptedException{
        System.out.println("接收到消息..."+content);
        byte[] body = message.getBody();
        MessageProperties properties = message.getMessageProperties();
        Thread.sleep(3000);
        System.out.println("消息处理完成=>"+content.getName());
    }


    @RabbitHandler
    public void recieveMessage1(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel) throws InterruptedException{
        System.out.println("接收到消息..."+content);
        byte[] body = message.getBody();
        MessageProperties properties = message.getMessageProperties();
        Thread.sleep(3000);
        System.out.println("消息处理完成=>"+content.getName());

//        channel.basicAck(deliveryTag,false);
    }

    @RabbitHandler
    public void recieveMessage2(OrderEntity content)throws InterruptedException{
        System.out.println("接收到消息..."+content);
    }

}