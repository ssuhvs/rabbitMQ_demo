package com.zonzie.rabbitTest;

import com.zonzie.domian.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author zonzie
 * @date 2018/7/13 14:22
 */
@RestController
@RequestMapping(value = "sender", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(tags = "rabbitTest")
@Slf4j
public class RabbitSender {

    @Resource(name = "myTemplate")
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    @ApiOperation(value = "发送string消息")
    public String send(String context) {
        System.out.println(context);
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
//        rabbitTemplate.convertAndSend("hello", context, correlationData);
//        rabbitTemplate.convertAndSend("test","helloTest");
//        rabbitTemplate.convertAndSend("com.zonzie.directtest","hellotest","helloTEST");
        rabbitTemplate.convertAndSend("com.zonzie.topictest", "com.zonzie.test",context);
//        rabbitTemplate.convertAndSend("hellotest","helloTEST");

        return "bingo!!!";
    }

    @GetMapping("/user")
    @ApiOperation(value = "发送userObject")
    public String sendUser() {
        User user = new User();
        user.setUsername("ybq");
        user.setPassword("123");
        rabbitTemplate.convertAndSend("object",user);
        return "bingo!!!";
    }
}
