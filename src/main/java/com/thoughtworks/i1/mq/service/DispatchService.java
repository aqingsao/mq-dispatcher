package com.thoughtworks.i1.mq.service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class DispatchService {
    MessageProducer producer;

    @Inject
    public DispatchService(ConnectionFactory connectionFactory, @Named("queueName")String queueName) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(session.createQueue(queueName));
    }

    public boolean sendMessage(Message message){
        try {
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
