package com.hyf.test;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 扩展测试内容
 *
 * @author baB_hyf
 * @date 2021/02/25
 */
public class Extension {

    /**
     * basicGet 方法的测试
     * 强烈不推荐使用的方法，因为其采用轮询的方式（Pull API）
     */
    @Test
    public void testBasicGet() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();
        GetResponse resp = channel.basicGet("queue1", true);
        if (resp == null) {
            System.out.println("No message received");
        }
        else {
            byte[] body = resp.getBody();
            System.out.println("Received: " + new String(body));
        }

        channel.close();
        conn.close();
    }

    /**
     * basic.cancel 相关功能的测试
     */
    @Test
    public void testCancel() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        /*
         * 1: 预取消息的最大大小，0无限制
         * 2: 预取消息数，0无限制
         * 3: 是否为全局配置，true表示针对于整个channel的限制，false表示针对于每个消费者（Consumer）的限制（默认）
         */
        channel.basicQos(1 << 6, 1, false);
        channel.basicQos(2, true);

        channel.basicConsume("queue5", new DefaultConsumer(channel) {

            /**
             * basicConsume成功的回调
             */
            @Override
            public void handleConsumeOk(String consumerTag) {
                super.handleConsumeOk(consumerTag);
                System.out.println("handleConsumeOk");

                throw new RuntimeException();
            }

            /**
             * basicConsume成功的回调
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("handleDelivery: " + new String(body));
                getChannel().basicCancel(consumerTag);
            }

            /**
             * 显示取消
             * basicCancel成功的回调
             */
            @Override
            public void handleCancelOk(String consumerTag) {
                System.out.println("handleCancelOk");
            }

            /**
             * 隐式取消
             * 消费者被取消的回调（除了手动调用basicCancel），如队列被删除
             */
            @Override
            public void handleCancel(String consumerTag) throws IOException {
                System.out.println("cancel Message: " + consumerTag);
            }

            /**
             * Called when a basic.recover-ok is received in reply to a basic.recover.
             * All messages received before this is invoked that haven't been ack'ed will be
             * re-delivered. All messages received afterwards won't be.
             */
            @Override
            public void handleRecoverOk(String consumerTag) {
                System.out.println("handleRecoverOk");
            }

            /**
             * 通道或连接被异常关闭的回调，返回异常对象
             * 也用于处理消息时产生的异常
             */
            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                System.out.println("handleShutdownSignal: ");
                System.out.println("\treason:" + sig.getReason());
                System.out.println("\treference:" + sig.getReference());
                System.out.println("\tisHardError:" + sig.isHardError());
                System.out.println("\tisInitiatedByApplication:" + sig.isInitiatedByApplication());
                System.out.println("\tException: " + sig);
            }
        });

        // channel.close();
        // conn.close();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * Connection::blocked 测试
     */
    @Test
    public void testBlocked() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();

        // 资源（内存或磁盘）不足而阻塞连接时收到通知
        conn.addBlockedListener(new BlockedListener() {
            @Override
            public void handleBlocked(String reason) throws IOException {
                System.out.println("Connection is now blocked:" + reason);
            }

            @Override
            public void handleUnblocked() throws IOException {
                System.out.println("Connection is now unblocked");
            }
        });

        Channel channel = conn.createChannel();
        channel.basicQos(1);

        channel.basicConsume("queue1", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("handleDelivery: " + new String(body));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignore) {
                }
                System.out.println("handleDelivery end");
            }
        });

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * alternate exchange 测试
     */
    @Test
    public void testAE() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        String msg = "Hello Alternate Exchange";

        Map<String, Object> args = new HashMap<>();
        args.put("alternate-exchange", "s-alternate-exchange");
        channel.exchangeDeclare("m-alternate-exchange", "direct", true, false, args);
        channel.exchangeDeclare("s-alternate-exchange", "fanout", true, false, null);
        channel.queueDeclare("m-alternate-queue", true, false, false, null);
        channel.queueDeclare("s-alternate-queue", true, false, false, null);
        channel.queueBind("m-alternate-queue", "m-alternate-exchange", "m1");
        channel.queueBind("s-alternate-queue", "s-alternate-exchange", "");

        channel.basicPublish("m-alternate-exchange", "m1", null, msg.getBytes());
        channel.basicPublish("m-alternate-exchange", "key1", null, msg.getBytes());

        channel.close();
        conn.close();
    }

    /**
     * CC BCC 测试
     */
    @Test
    public void testSendRouting() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();

        String msg = "Hello send routing";

        channel.exchangeDeclare("ex-send-routing", "direct", true, false, null);
        channel.queueDeclare("ex-sr-queue1", true, false, false, null);
        channel.queueDeclare("ex-sr-queue2", true, false, false, null);
        channel.queueDeclare("ex-sr-queue3", true, false, false, null);
        channel.queueBind("ex-sr-queue1", "ex-send-routing", "sr1");
        channel.queueBind("ex-sr-queue2", "ex-send-routing", "sr2");
        channel.queueBind("ex-sr-queue3", "ex-send-routing", "sr3");

        // add routing key
        Map<String, Object> headers = new HashMap<>();
        headers.put("CC", Arrays.asList("sr1", "sr2"));
        headers.put("BCC", Arrays.asList("sr1", "sr2", "sr3"));
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(headers).build();
        channel.basicPublish("ex-send-routing", "sr1", true, properties, msg.getBytes());

        channel.close();
        conn.close();
    }

    /**
     * 管理界面中 Queues内的 Consumers栏目查看 测试
     */
    @Test
    public void testShowManagementQueueConsumers() throws IOException, TimeoutException {
        Connection conn = ConnectionUtil.getConnection();
        Channel channel = conn.createChannel();
        channel.basicConsume("queue2", new DefaultConsumer(channel) {

            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                System.out.println("handleShutdownSignal: ");
                System.out.println("\treason:" + sig.getReason());
                System.out.println("\treference:" + sig.getReference());
                System.out.println("\tisHardError:" + sig.isHardError());
                System.out.println("\tisInitiatedByApplication:" + sig.isInitiatedByApplication());
                System.out.println("\tException: " + sig);
            }
        });
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignore) {
        }
    }
}
