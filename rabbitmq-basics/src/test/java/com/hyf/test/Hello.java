package com.hyf.test;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author baB_hyf
 * @date 2021/02/15
 */
public class Hello {

    /**
     * 测试生产者发布消息
     */
    @Test
    public void hello() {
        String msg = "Hello World2";

        try (Connection conn = ConnectionUtil.getConnection();
             Channel channel = conn.createChannel() /* 相当于Statement */) {

            /*
             * 1: 交换机名称
             * 2: 队列名称或路由key(交换机名称不为空)
             * 3: 是否强制（为了进行更好的控制）
             * 4: 不支持
             * 5: 消息的相关属性配置
             * 6: 消息体
             */
            channel.basicPublish("", "queue5", true, false, null, msg.getBytes());
            // 指定消息持久化
            channel.basicPublish("", "queue5", true, false, MessageProperties.PERSISTENT_BASIC, msg.getBytes());
            System.out.println("发送成功：" + msg);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试消费者消费消息
     */
    @Test
    public void testConsumer() {
        try (Connection conn = ConnectionUtil.getConnection(); Channel channel = conn.createChannel()) {

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleCancel(String consumerTag) throws IOException {
                    System.out.println("handleCancel: " + consumerTag);
                }

                @Override
                public void handleCancelOk(String consumerTag) {
                    System.out.println("handleCancelOk: " + consumerTag);
                }

                /*
                 * 1: 与消费者相关联的消费者标签
                 * 2: 打包好的消息数据
                 * 3: 消息的内容头属性
                 * 4: 消息内容
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.printf("handleDelivery: \n%s \n%s \n%s \n%s%n", consumerTag, envelope, properties, new String(body));
                }

                @Override
                public void handleRecoverOk(String consumerTag) {
                    System.out.println("handleRecoverOk: " + consumerTag);
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.out.println("handleShutdownSignal: " + consumerTag + " " + sig);
                }
            };

            /*
             * 1: 队列名称
             * 2: 是否自动确认
             * 3: 消息回调
             */
            String msg = channel.basicConsume("queue1", true, consumer);
            System.out.println("接收消息：" + msg);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建队列
     */
    @Test
    public void testCreateQueue() {
        try (Connection conn = ConnectionUtil.getConnection();
             Channel channel = conn.createChannel()) {

            /*
             * 1: 队列名称
             * 2: 消息是否持久化
             * 3: 队列是否为当前连接独有的，即只能给当前连接使用
             * 4: 队列无连接时是否自动删除
             * 5: 队列相关属性配置
             */
            AMQP.Queue.DeclareOk queue2 =
                    channel.queueDeclare("queue2", false, false, false, null); // 创建队列

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建交换机
     */
    @Test
    public void testCreateExchange() {
        try (Connection conn = ConnectionUtil.getConnection();
             Channel channel = conn.createChannel()) {

            /*
             * 1: 交换机名称
             * 2: 交换机类型
             * 3: 交换机是否持久化
             * 4: 交换机无连接时是否自动删除
             * 5: 交换机是否为私有
             * 6: 交换机相关属性配置
             */
            AMQP.Exchange.DeclareOk ex2 =
                    channel.exchangeDeclare("ex2", BuiltinExchangeType.FANOUT, true, false, false, null);

            /*
             * 1: 队列名称（destination）
             * 2: 交换机名称（source）
             * 3: 路由key
             * 4: 交换机参数
             */
            AMQP.Exchange.BindOk bindOk =
                    channel.exchangeBind("queue1", "ex1", "1", null);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
