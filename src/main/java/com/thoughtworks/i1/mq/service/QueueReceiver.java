package com.thoughtworks.i1.mq.service;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

// Used to retrieve message from 2 queues(1 messgae queue and 1 failure queue), and put failure message to another queue
public class QueueReceiver implements AutoCloseable{
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;

    public void sendFailed(Message message) {

    }

    @Override
    public void close() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}