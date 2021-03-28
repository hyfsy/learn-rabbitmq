package com.hyf.test;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author baB_hyf
 * @date 2021/03/07
 */
public class TTLTests {

    @Test
    public void testTTL_producer() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        String msg = "Hello ttl";
        channel.basicPublish("", "q_ttl", null, msg.getBytes());

        channel.close();
        conn.close();
    }

    @Test
    public void testTTL_consumer() throws IOException, TimeoutException, InterruptedException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        channel.basicConsume("q_ttl", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
                long deliveryTag = envelope.getDeliveryTag();
                System.out.println("Get message: " + new String(body));
                System.out.println("isRedeliver: " + envelope.isRedeliver());
                System.out.println(System.currentTimeMillis() / 1000);

                // 此处通过设置queue的属性，所以看不到

                // System.out.println("Headers: " + properties.getHeaders());
                // System.out.println("Expiration: " + properties.getExpiration());
                // System.out.println("Timestamp: " + properties.getTimestamp());
                // System.out.println("message-ttl: " + properties.getHeaders().get("message-ttl"));
                // System.out.println("expires: " + properties.getHeaders().get("expires"));
                System.out.println("=======================================");

                channel.basicReject(deliveryTag, true);

            }
        });

        Thread.currentThread().join();
    }
}
