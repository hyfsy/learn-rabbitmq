import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 测试 @EnableRabbit 注解是否会创建 RabbitTemplate
 * 不会
 *
 * @author baB_hyf
 * @date 2021/03/07
 */
public class Test3 {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(RabbitConfiguration.class);

        RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry = app.getBean(RabbitListenerEndpointRegistry.class);
        System.out.println(rabbitListenerEndpointRegistry);

        AmqpTemplate rabbitTemplate = app.getBean(AmqpTemplate.class);
        System.out.println(rabbitTemplate);

        // RabbitTemplate rabbitTemplate = app.getBean(RabbitTemplate.class);
        // System.out.println(rabbitTemplate);
    }

    @EnableRabbit
    static class RabbitConfiguration {

    }
}
