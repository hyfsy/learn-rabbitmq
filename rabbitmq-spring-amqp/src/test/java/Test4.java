import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试 @conditionBean.isMandatory(#root) 条件
 *
 * @author baB_hyf
 * @date 2021/03/07
 */
public class Test4 {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        RabbitTemplate template = app.getBean(RabbitTemplate.class);
        template.setMandatoryExpressionString("@conditionBean.isMandatory(#root)");
        Message message = new Message("hello".getBytes(), new MessageProperties());
        System.out.println(template.isMandatoryFor(message)); // #root
    }

    @Configuration
    static class RabbitConfiguration {

        @Bean
        public RabbitTemplate rabbitTemplate() {
            CachingConnectionFactory factory = new CachingConnectionFactory();
            return new RabbitTemplate(factory);
        }

        @Bean
        public ConditionBean conditionBean() {
            return new ConditionBean();
        }

        static class ConditionBean {
            public boolean isMandatory(Message message) {
                System.out.println(message);
                return message != null;
            }
        }
    }
}
