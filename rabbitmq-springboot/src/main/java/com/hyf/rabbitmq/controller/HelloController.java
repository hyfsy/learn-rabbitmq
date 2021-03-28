package com.hyf.rabbitmq.controller;

import com.hyf.rabbitmq.pojo.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author baB_hyf
 * @date 2021/02/17
 */
@RestController
public class HelloController {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping("/producer")
    public void producer() {
        String msg = "Hello World";

        // simple
        amqpTemplate.convertAndSend(msg);

        // work
        amqpTemplate.convertAndSend("queue1", msg);

        // pub/sub
        amqpTemplate.convertAndSend("ex1", "", msg);

        // route
        amqpTemplate.convertAndSend("ex1", "1", msg);

        // full
        MessagePostProcessor messagePostProcessor = message -> {
            System.out.println(message);
            return message;
        };
        amqpTemplate.convertAndSend("queue1", "key", "msg", messagePostProcessor);

        User user = new User(1, "张三");
        byte[] bytes = SerializationUtils.serialize(user); // 转换为发送字节数组的形式
    }

    @RequestMapping("/consumer")
    public void consumer() {
        Message receiveMsg = amqpTemplate.receive("queue1", 1000L);
        if (receiveMsg != null) {
            byte[] body = receiveMsg.getBody();
        }

        String msg = amqpTemplate.receiveAndConvert("queue1", 1000L,
                new ParameterizedTypeReference<String>() {
            // 类型转换
        });
    }
}
