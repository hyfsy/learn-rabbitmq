package com.hyf.test.mode;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RPC模式
 *
 * @author baB_hyf
 * @date 2021/02/21
 */
public class RpcMode {

    /**
     * 测试RPC使用
     */
    @Test
    public void testRpcClient() {
        String msg = "Hello RPC";
        try (Connection conn = ConnectionUtil.getConnection();
             Channel channel = conn.createChannel()) {

            String correlationId = "uniqueId";
            String callbackQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                    .replyTo(callbackQueueName) // 回调队列名称，RPC请求后返回调用的队列
                    // .replyTo("amq.rabbitmq.reply-to") // 会将回调发送到默认交换机中（""）
                    .correlationId(correlationId)
                    .build();
            channel.basicPublish("", "rpc_queue", props, msg.getBytes());

            channel.basicConsume(callbackQueueName, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    // 校验
                    if (correlationId.equals(properties.getCorrelationId())) {
                        // 处理回调请求
                        System.out.println("Success");
                    }
                }
            });

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRpcServer() {
        String msg = "RPC Callback";
        try (Connection conn = ConnectionUtil.getConnection();
             Channel channel = conn.createChannel()) {

            channel.basicConsume("rpc_queue", new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    // 服务器处理

                    // 回调发送
                    channel.basicPublish("", properties.getReplyTo(), properties, msg.getBytes());
                }
            });

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
