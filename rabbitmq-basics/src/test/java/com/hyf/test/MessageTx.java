package com.hyf.test;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息的事务
 * <p>
 * 消息确认与return机制
 *
 * @author baB_hyf
 * @date 2021/02/17
 */
public class MessageTx {

    @Test
    public void testTx() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        String msg = "Text Tx";

        channel.txSelect(); // 开启事务
        try {
            channel.basicPublish("queue1", "", null, msg.getBytes()); // 发送消息
            channel.txCommit(); // 提交事务
        } catch (Exception e) {
            channel.txRollback(); // 回滚事务
        } finally {
            channel.close();
        }
    }

    @Test
    public void testAck() throws IOException, TimeoutException, InterruptedException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        String msg = "Text Confirm";

        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {

            /*
             * 1: 消息标识
             * 2: 是否为批量消息
             */
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息成功发送到交换机");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息发送到交换机失败");
            }
        });

        channel.addReturnListener(new ReturnListener() {

            /*
             * 1: 回复状态码
             * 2: 回复信息
             * 3: 交换机名称
             * 4: 队列名称或路由key
             * 5: 消息属性
             * 6: 消息体
             */
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消息从交换机发送到队列失败");
            }
        });

        // 发送单条消息
        channel.basicPublish("queue1", "", true, null, msg.getBytes()); // 指定销毁

        // 批量发送消息
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("queue1", "", true, null, msg.getBytes()); // 指定销毁
        }
        boolean confirms = channel.waitForConfirms(1000L);// 等待消息确认
    }

    /**
     * 手动消息确认
     */
    @Test
    public void testHandAsk() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        channel.basicQos(1); // 消费者一次只接收一条未确认的消息

        // 关闭自动确认
        channel.basicConsume("queue1", false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                /*
                 * 1: 具体消息的标签
                 * 2: 是否运行多个消息同时确认
                 */
                channel.basicAck(envelope.getDeliveryTag(), true);

                /*
                 * 1: 具体消息的标签
                 * 2: 是否运行多个消息同时拒绝
                 * 3: 消息是否重新排队，false则丢弃
                 */
                // channel.basicNack(envelope.getDeliveryTag(), true, true);
                // channel.basicReject(envelope.getDeliveryTag(), true); // 区别就是无法处理多个拒绝
            }
        });

    }
}
