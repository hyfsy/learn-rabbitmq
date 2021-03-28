import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello
 *
 * @author baB_hyf
 * @date 2021/03/01
 */
public class Test {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        MessageListenerContainer listenerContainer = app.getBean(MessageListenerContainer.class);
        System.out.println(listenerContainer.getClass().getName());
        app.close();
    }
}
