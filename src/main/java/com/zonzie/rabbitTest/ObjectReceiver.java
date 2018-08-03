package com.zonzie.rabbitTest;

import com.google.common.base.Objects;
import com.rabbitmq.client.Channel;
import com.zonzie.domian.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zonzie
 * @date 2018/7/13 15:27
 */
@Slf4j
@Component
public class ObjectReceiver {

     /**
        消息的标识，false只确认当前一个消息收到，true确认所有consumer获得的消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        ack返回false，并重新回到队列，api里面解释得很清楚
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        拒绝消息
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        如果消息没有到exchange,则confirm回调,ack=false
        如果消息到达exchange,则confirm回调,ack=true
        exchange到queue成功,则不回调return
        exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
    */
    @RabbitHandler
    @RabbitListener(containerFactory = "myListenContainer", queues = "object")
    public void process(User user, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        System.out.println(user);
        channel.basicAck(deliveryTag,false);
        System.out.print("这里是接收者1答应消息： ");
        System.out.println("SYS_TOPIC_ORDER_CALCULATE_ZZ_FEE process1  : " + user);
    }

    @RabbitHandler
    @RabbitListener(containerFactory = "myListenContainer",queues = {"hello","test"})
    public void process(String hello, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) throws IOException {
        System.out.println("Receiver: " + hello);
        if(Objects.equal(hello, "tttt")) {
            channel.basicReject(deliverTag, false);
        }
        channel.basicAck(deliverTag, false);
    }

    @RabbitHandler
    @RabbitListener(containerFactory = "myListenContainer",queues = {"test"})
    public void process2(String hello, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) throws IOException {
        System.out.println("Receiver222: " + hello);
        channel.basicAck(deliverTag, false);
    }

}
