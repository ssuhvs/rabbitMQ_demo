package com.zonzie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author zonzie
 * @date 2018/7/13 17:48
 */
@Configuration
@Component
@Slf4j
public class AmqpTemplateConfig{

    @Bean(name = "myTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 设置messageConverter
        rabbitTemplate.setMessageConverter(messageConverter());
        // 设置重试次数
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(500);
        exponentialBackOffPolicy.setMultiplier(10.0);
        exponentialBackOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        rabbitTemplate.setRetryTemplate(retryTemplate);
        // 设置回调方法
        rabbitTemplate.setConfirmCallback(new ConfirmCallbackDemo());
        // 失败后return回调
        rabbitTemplate.setReturnCallback(new ReturnCallBackDemo());
        // return 回调需要设置,不然不会生效
        rabbitTemplate.setMandatory(true);
        // 事务
//        rabbitTemplate.setChannelTransacted(true);
//        rabbitTemplate.setExchange();
        return rabbitTemplate;
    }

    @Bean(name="myListenContainer")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, @Qualifier("rabbitTransactionManager") PlatformTransactionManager manager) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // 设置messageConverter
        factory.setMessageConverter(messageConverter());
        // 设置事务管理
//        factory.setChannelTransacted(true);
//        factory.setTransactionManager(manager);
        factory.setConnectionFactory(connectionFactory);
        // 设置手动应答模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean(name = "rabbitTransactionManager")
    public RabbitTransactionManager getTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

//    @Bean
//    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        container.setMessageConverter(messageConverter());
//        container.setQueues(new Queue("hello"), new Queue("object"));
//        container.setExposeListenerChannel(true);
//        container.setMaxConcurrentConsumers(1);
//        container.setConcurrentConsumers(1);
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        container.setMessageListener(new ChannelAwareMessageListener() {
//
//            @Override
//            public void onMessage(Message message, com.rabbitmq.client.Channel channel) throws Exception {
//                byte[] body = message.getBody();
//                log.info("消费端接收到消息 : " + new String(body));
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//            }
//        });
//        return container;
//    }

    private Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
