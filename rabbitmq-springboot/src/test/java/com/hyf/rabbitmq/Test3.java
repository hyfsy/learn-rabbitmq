package com.hyf.rabbitmq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 添加 @EnableRabbit 会创建注解扫描监听器，自动装配会自动通过 RabbitAnnotationDrivenConfiguration 启用
 * 自动配置还会创建 RabbitTemplate 和 AmqpAdmin 对象
 *
 * @author baB_hyf
 * @date 2021/03/07
 */
public class Test3 {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .sources(RabbitConfiguration.class)
                .run(args);

        CachingConnectionFactory factory = app.getBean(CachingConnectionFactory.class);
        System.out.println(factory);

        AmqpTemplate amqpTemplate = app.getBean(AmqpTemplate.class);
        System.out.println(amqpTemplate);

        RabbitTemplate rabbitTemplate = app.getBean(RabbitTemplate.class);
        System.out.println(rabbitTemplate);

        AmqpAdmin admin = app.getBean(AmqpAdmin.class);

        RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry = app.getBean(RabbitListenerEndpointRegistry.class);
        System.out.println(rabbitListenerEndpointRegistry);

        RabbitListenerAnnotationBeanPostProcessor processor = app.getBean(RabbitListenerAnnotationBeanPostProcessor.class);
        System.out.println(processor);
    }

    @SpringBootApplication
    // @EnableRabbit
    static class RabbitConfiguration {

    }
}
