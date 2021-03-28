package com.hyf.rabbitmq.service;

import com.hyf.rabbitmq.pojo.User;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 接受消息处理类
 * <p>
 * 能传字符串、字节数组、对象
 *
 * @author baB_hyf
 * @date 2021/02/17
 */
@Service
@RabbitListener(queues = {"queue1", "queue2"})
public class MessageService {

    @RabbitHandler
    public void receiveBytes(byte[] bytes) {
        System.out.println("receiveBytes 接受消息: " + new String(bytes));
    }

    @RabbitHandler
    public void receiveString(String msg) {
        System.out.println("receiveString 接受消息: " + msg);
    }

    @RabbitHandler
    public void receiveObject(User user) {
        System.out.println("receiveObject 接受消息: " + user);
    }
}
