package com.atguigu.gulimail.order;

import com.atguigu.gulimail.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@SpringBootTest
class GulimailOrderApplicationTests {

    @Resource
    AmqpAdmin amqpAdmin;

    @Resource
    RabbitTemplate rabbitTemplate;

    /**
     * 如何创建Exchange    Queue  Binding  1)使用AmqpAdmin进行创建
     * 2)如何收发消息
     */

    @Test
    public void createExchange(){
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exhchange创建成功");
    }


    @Test
    public void createQueue(){
        Queue queue = new Queue("hello-java-exchange",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue创建成功");
    }


    @Test
    public void createBinding(){
        Binding binding = new Binding("hello-java-exchange",Binding.DestinationType.QUEUE,"hello-java-exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding创建成功");
    }

    /**
     * 方式2
     * rabbitTemplate消息发送
     */
    @Test
    public void sendMessageTest(){
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setCreateTime(new Date());
        reasonEntity.setName("哈哈哈");
        
        //1.发送消息
        String msg = "Hello World";
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",msg);
        log.info("消息发送完成");
    }
}
