package com.hyf.test;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author baB_hyf
 * @date 2021/03/15
 */
public class Cluster_Test {

    private Connection conn;
    private Channel    channel;

    @Before
    public void before() throws IOException, TimeoutException {
        ConnectionFactory factory = ConnectionUtil.getDefaultConnectionFactory();
        factory.setHost("192.168.190.188");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setConnectionTimeout(1000); // quorum 连接和此无关？？？
        conn = ConnectionUtil.getConnection(factory);
        channel = conn.createChannel();
    }

    // @After
    // public void after() throws IOException, TimeoutException {
    //     channel.close();
    //     conn.close();
    // }


    @Test
    public void testOne() throws IOException {
        String msg = "Hello World2";
        channel.basicPublish("", "q2", true, false, MessageProperties.PERSISTENT_BASIC, msg.getBytes());
        System.out.println("发送成功：" + msg);
    }

    @Test
    public void testTwo() throws IOException {

        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.printf("handleDelivery: \n%s \n%s \n%s \n%s%n", consumerTag, envelope, properties, new String(body));
            }
        };

        String msg = channel.basicConsume("q2", true, consumer);
        System.out.println("接收消息：" + msg);

    }
}
