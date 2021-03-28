package com.hyf.test.mode;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 路由模式 - direct
 *
 * @author baB_hyf
 * @date 2021/02/17
 */
public class RouteMode {

    @Test
    public void producer() throws IOException, TimeoutException {
        Scanner scanner = new Scanner(System.in);

        String msg = "";
        while (!"quit".equals(msg)) {
            Connection conn = ConnectionUtil.getConnection();
            Channel channel = conn.createChannel();
            System.out.print("请输入消息：");
            msg = scanner.nextLine();
            // 路由
            if (msg.startsWith("1-")) {
                channel.basicPublish("ex2", "1", null, msg.getBytes());
            }
            else if (msg.startsWith("2-")) {
                channel.basicPublish("ex2", "2", null, msg.getBytes());
            }
            // 订阅
            else {
                channel.basicPublish("ex1", "", null, msg.getBytes());
            }
            channel.close();
            conn.close();
        }

    }

    @Test
    public void consumer1() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();
        Thread t1 = Thread.currentThread();
        channel.basicConsume("queue1", true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                if ("quit".equals(msg)) {
                    try {
                        channel.close();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    conn.close();
                    t1.interrupt();
                    return;
                }
                System.out.println("消费者1消费消息：" + msg);
            }
        });

        try {
            t1.join();
        } catch (InterruptedException e) {
            System.out.println("消费者1退出");
        }
    }

    @Test
    public void consumer2() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();
        Thread t1 = Thread.currentThread();
        channel.basicConsume("queue2", true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                if ("quit".equals(msg)) {
                    try {
                        channel.close();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    conn.close();
                    t1.interrupt();
                    return;
                }
                System.out.println("消费者2消费消息：" + msg);
            }
        });

        try {
            t1.join();
        } catch (InterruptedException e) {
            System.out.println("消费者2退出");
        }
    }
}
