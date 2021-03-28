package com.hyf.test.mode;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 工作模式
 *
 * @author baB_hyf
 * @date 2021/02/17
 */
public class WorkMode {

    @Test
    public void producer() throws IOException, TimeoutException {
        Scanner scanner = new Scanner(System.in);
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        String msg = "";
        int consumerSize = 2;
        while (!("quit".equals(msg) && --consumerSize <= 0)) {
            System.out.print("请输入消息：");
            msg = scanner.nextLine();
            channel.basicPublish("", "queue1", null, msg.getBytes());
        }

        channel.close();
        conn.close();
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
