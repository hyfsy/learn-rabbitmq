package com.hyf.test;

import com.hyf.rabbitmq.util.ConnectionUtil;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.MicrometerMetricsCollector;
import com.rabbitmq.client.impl.StandardMetricsCollector;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * API相关测试
 *
 * @author baB_hyf
 * @date 2021/02/27
 */
public class API {

    private Connection conn;
    private Channel    channel;

    @Test
    public void testOne() {
        // channel.queueDeclarePassive("");
        // conn.addShutdownListener();
        channel.addShutdownListener(e -> {
            System.out.println(1);
        });
        channel.addShutdownListener(System.out::println);
        channel.notifyListeners();
    }

    @Test
    public void testTwo() throws IOException {
        RecoverableConnection recoverableConnection = (RecoverableConnection)conn;
        RecoverableChannel recoverableChannel = (RecoverableChannel)channel;
        recoverableConnection.addRecoveryListener(new RecoveryListener() {

            @Override
            public void handleRecoveryStarted(Recoverable recoverable) {
                System.out.println("Start to recovery");
            }

            @Override
            public void handleRecovery(Recoverable recoverable) {
                System.out.println("Recovery completed");
            }
        });
    }

    @Test
    public void testThree() {
        ConnectionFactory factory = ConnectionUtil.getDefaultConnectionFactory();
        factory.setMetricsCollector(new StandardMetricsCollector());

        LoggingMeterRegistry registry = new LoggingMeterRegistry();
        MicrometerMetricsCollector collector = new MicrometerMetricsCollector(registry);
        factory.setMetricsCollector(collector);
    }

    @Before
    public void before() throws IOException, TimeoutException {
        conn = ConnectionUtil.getConnection();
        channel = conn.createChannel();
    }

    @After
    public void after() throws IOException, TimeoutException {
        channel.close();
        conn.close();
    }
}
