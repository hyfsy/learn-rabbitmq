package com.hyf.rabbit.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * @author baB_hyf
 * @date 2021/03/01
 */
public class MyMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println("Receive message: " + message);
    }

    public void handle(Object message) {
        System.out.println("Receive message: " + message);
    }
}
