package com.hyf.rabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author baB_hyf
 * @date 2021/02/15
 */
public class ConnectionUtil {

    private static final ConnectionFactory factory;

    static {
        factory = new ConnectionFactory();

        /* default value:
         *      host:        localhost
         *      port:        5672 / 5671(TLS)
         *      virtualHost: /
         *      username:    guest
         *      password:    guest
         */

        // default
        factory.setHost("192.168.190.188");
        factory.setPort(5672);
        factory.setVirtualHost("vhost1");
        factory.setUsername("hyfsy");
        factory.setPassword("11111");

        // try {
        //     // amqp://userName:password@hostName:portNumber/virtualHost
        //     factory.setUri("amqp://guest:guest@localhost:5672//");
        // } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
        //     throw new RuntimeException("ConnectionFactory create failed!");
        // }

        // heartbeat
        factory.setRequestedHeartbeat(60); // 60秒心跳超时时间，会不断请求客户端查看是否能访问到该连接

        // client properties
        Map<String, Object> clientProperties = new HashMap<>();
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("consumer_cancel_notify", "true"); // 没测试出basic.cancel，没用？
        // capabilities.put("connection.blocked", false); // 默认开启
        clientProperties.put("capabilities", capabilities);
        factory.setClientProperties(clientProperties);
        factory.setConnectionTimeout(2000);
    }

    public static ConnectionFactory getDefaultConnectionFactory() {
        return factory.clone();
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        return getDefaultConnectionFactory().newConnection();
    }

    public static Connection getConnection(ConnectionFactory factory) throws IOException, TimeoutException {
        return factory.newConnection();
    }
}
