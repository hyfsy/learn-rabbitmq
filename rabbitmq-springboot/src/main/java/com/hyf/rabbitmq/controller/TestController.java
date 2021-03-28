package com.hyf.rabbitmq.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author baB_hyf
 * @date 2021/03/17
 */
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("1")
    public void one() {
        rabbitTemplate.convertAndSend("q1", "hello cluster");
        System.out.println(1);
    }
}
