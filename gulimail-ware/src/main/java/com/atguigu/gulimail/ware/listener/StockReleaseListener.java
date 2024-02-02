package com.atguigu.gulimail.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimail.common.to.mq.OrderTo;
import com.atguigu.gulimail.common.to.mq.StockDetailTo;
import com.atguigu.gulimail.common.to.mq.StockLockedTo;
import com.atguigu.gulimail.common.utils.R;
import com.atguigu.gulimail.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimail.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimail.ware.service.WareSkuService;
import com.atguigu.gulimail.ware.vo.OrderVo;
import com.rabbitmq.client.Channel;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author:guoyaqi
 * @Date: 2023/12/4 23:29
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to , Message msg, Channel channel) throws IOException {


        try{
            wareSkuService.unlockStock(to);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(msg.getMessageProperties().getDeliveryTag(),true);

        }

    }

    @RabbitHandler
    public void handlerOrderCloseRelease(OrderTo orderTo, Message msg, Channel channel) throws IOException {
        System.out.println("订单关闭准备解锁库存...");

        try {
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(msg.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
