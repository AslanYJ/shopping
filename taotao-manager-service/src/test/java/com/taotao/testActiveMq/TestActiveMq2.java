package com.taotao.testActiveMq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import javax.print.attribute.standard.Destination;

public class TestActiveMq2 {
    @Test
    public void testQueueProducer() {
        //1.读取配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
        //2.从容器中获得JMSTemplate对象
        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
        //3.从容器中获得一个Destination对象
        Queue queue = (Queue)applicationContext.getBean("queueDestination");
        //4.使用JMSTemplate对象发送消息，需要知道Destination
        jmsTemplate.send(queue,new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage("spring activemq test");
                return textMessage;
            }
        });

    }
}
