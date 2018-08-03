package com.zonzie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author zonzie
 * @date 2018/7/14 15:31
 */
@Slf4j
@Component
@Configuration
public class ConfirmCallbackDemo implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info(" 消息id:" + correlationData);
        if (ack) {
            log.info("消息发送确认成功");
        } else {
            log.info("消息发送确认失败:" + cause);
        }
    }
}
