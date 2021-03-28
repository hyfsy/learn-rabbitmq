package com.hyf.rabbitmq.service;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author baB_hyf
 * @date 2021/02/17
 */
@Service
public class ConfirmAndReturnService implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate; // 使用RabbitMQ的AmqpTemplate实现

    @PostConstruct
    public void init() {
        // 注册消息确认与return回调
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        // 监听消息确认结果
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        // 监听消息return结果
    }
}
