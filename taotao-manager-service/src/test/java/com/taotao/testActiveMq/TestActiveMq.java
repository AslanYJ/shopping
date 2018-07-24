package com.taotao.testActiveMq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.http.auth.AuthOption;
import org.junit.Test;

import javax.jms.*;

public class TestActiveMq {
    //测试消息队列的发送者
    @Test
    public void testActiveMqProducer() throws Exception{
        //1.创建一个连接的工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.208.40:61616");
        //2.使用ConnectionFactory创建一个Connection对象
        Connection connection = connectionFactory.createConnection();
        //3.开启连接
        connection.start();
        //4.使用Connection对象创建一个Session对象
        //第一个参数：是否开启事务(分布式事务)。true：开启事务，第二个参数忽略。
        //第二个参数：当第一个参数为false时，才有意义。消息的应答模式。1、自动应答2、手动应答。一般是自动应答。
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.使用Session对象创建一个Destination对象，（topic、queue），此处创建一个Queue对象
        //topic的话就是
//        Destination topic = session.createTopic();
        Destination queue = session.createQueue("test-queue");
        //6.使用Session对象创建一个Producer对象。
        MessageProducer producer = session.createProducer(queue);
        //7.创建一个Message对象，创建一个TextMessage对象
        TextMessage  textMessage  = session.createTextMessage("hello activeMq,this is my first 1");
        //8.使用生产者发送信息
        producer.send(textMessage);
        //9.关闭资源
        producer.close();
        session.close();
        connection.close();

    }
    //测试消息的接受者
    @Test
    public void testQueueConsumer() throws Exception{
        //1.创建一个连接的工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.208.40:61616");
        //2.使用ConnectionFactory创建一个Connection对象
        Connection connection = connectionFactory.createConnection();
        //3.开启连接
        connection.start();
        //4.使用Connection对象创建一个Session对象
        //第一个参数：是否开启事务(分布式事务)。true：开启事务，第二个参数忽略。
        //第二个参数：当第一个参数为false时，才有意义。消息的应答模式。1、自动应答2、手动应答。一般是自动应答。
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.使用Session对象创建一个Destination对象，（topic、queue），此处创建一个Queue对象
        Destination queue = session.createQueue("test-queue");
        //6.使用Session对象创建一个Consumer对象。
        MessageConsumer consumer = session.createConsumer(queue);
        //7.接收消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {

                try {
                    TextMessage textMessage = (TextMessage) message;
                    String text = null;
                    text = textMessage.getText();
                    System.out.println(text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }

            }
        });
        System.in.read();
        //9.关闭资源
        consumer.close();
        session.close();
        connection.close();
    }

}
