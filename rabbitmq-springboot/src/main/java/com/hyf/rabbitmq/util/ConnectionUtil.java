package com.hyf.rabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author baB_hyf
 * @date 2021/02/15
 */
public class ConnectionUtil {

    public static ConnectionFactory createDefaultConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.190.188");
        factory.setPort(5672);
        factory.setVirtualHost("vhost1");
        factory.setUsername("hyfsy");
        factory.setPassword("11111");
        return factory;
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        return createDefaultConnectionFactory().newConnection();
    }

    public static Connection getConnection(ConnectionFactory factory) throws IOException, TimeoutException {
        return factory.newConnection();
    }
}
