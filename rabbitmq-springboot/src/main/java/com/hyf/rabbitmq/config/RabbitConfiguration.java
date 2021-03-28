package com.hyf.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java代码创建队列和交换机并进行绑定
 *
 * @author baB_hyf
 * @date 2021/02/17
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    public Queue queue1() {
        Queue queue11 = new Queue("queue1", true, false, false, null);
        queue11.setActualName("queue1");
        return queue11;
    }

    @Bean
    public Queue queue2() {
        Queue queue11 = new Queue("queue2", true, false, false, null);
        queue11.setActualName("queue2");
        return queue11;
    }

    @Bean
    public Queue queue11() {
        Queue queue11 = new Queue("queue11", true, false, false, null);
        queue11.setActualName("queue11");
        return queue11;
    }

    @Bean
    public Exchange ex11() {
        FanoutExchange ex11 = new FanoutExchange("ex11", true, false, null);
        // DirectExchange ex12 = new DirectExchange("ex12");
        System.out.println(ex11.getType());
        return ex11;
    }

    @Bean
    public Binding binding11_1() {
        Binding binding = new Binding("queue11", Binding.DestinationType.QUEUE, "ex11", "1", null);
        System.out.println(binding.isDestinationQueue());
        return binding;
    }

    @Bean
    public Binding binding11_2(Queue queue11, Exchange ex11) {
        Binding binding = BindingBuilder.bind(queue11).to(ex11).with("1").noargs();
        System.out.println(binding.isDestinationQueue());
        return binding;
    }

    @Bean
    public Binding binding11_3() {
        Binding binding = BindingBuilder.bind(queue11()).to(ex11()).with("1").noargs();
        System.out.println(binding.isDestinationQueue());
        return binding;
    }
}
