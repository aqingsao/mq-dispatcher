package com.thoughtworks.i1.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Hello world!
 */
public class App {

    public static final String BROKER_URL = "tcp://localhost:61618/";

    public static void main(String[] args) throws Exception {
        thread(new HelloWorldProducer(1), true);
        thread(new HelloWorldProducer(2), false);
        thread(new HelloWorldConsumer(2), true);
        thread(new HelloWorldConsumer(2), true);
        Thread.sleep(1000);
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class HelloWorldProducer implements Runnable {

        private int deviceId;

        public HelloWorldProducer(int deviceId) {
            this.deviceId = deviceId;
        }

        public void run() {
            try {
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("TEST.FOO");

                // Create a MessageProducer from the Session to the Topic or Queue
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);

                // Create a messages
                String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
                TextMessage message = session.createTextMessage(text);
                message.setIntProperty("deviceId", deviceId);

                // Tell the producer to send the message
                System.out.println("Sent message: "+ this.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);

                // Clean up
//                session.close();
//                connection.close();
            }
            catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
    }

    public static class HelloWorldConsumer implements Runnable, ExceptionListener, MessageListener {
        private int deviceId;

        public HelloWorldConsumer(int deviceId) {
            this.deviceId = deviceId;
        }

        public void run() {
            try {

                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                connection.setExceptionListener(this);

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("TEST.FOO");

                // Create a MessageConsumer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination, "deviceId=" + deviceId);

                // Wait for a message
                consumer.setMessageListener(this);

//                consumer.close();
//                session.close();
//                connection.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }

        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }

        @Override
        public void onMessage(Message message) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    String text = textMessage.getText();
                    System.out.println("Received: " + text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Received: " + message);
            }
        }
    }
}