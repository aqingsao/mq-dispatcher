package com.thoughtworks.i1.mq.jms;

import com.thoughtworks.i1.commons.SystemException;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

public class QueueReceiver implements AutoCloseable {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(QueueReceiver.class);
    private final Connection connection;
    private final Queue queue;
    private final Session session;

    @Inject
    public QueueReceiver(MessageListener listener, ConnectionFactory connectionFactory, @Named("queueName") String queueName) {
        this(listener, connectionFactory, queueName, false, Session.AUTO_ACKNOWLEDGE);
    }

    public QueueReceiver(MessageListener listener, ConnectionFactory connectionFactory, String queueName, boolean transacted, int acknowledgeMode) {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(transacted, acknowledgeMode);
            queue = session.createQueue(queueName);
            start(listener);
        } catch (JMSException e) {
            throw new SystemException("Could not connect to destination " + queueName, e);
        }
    }

    public void start(MessageListener messageListener) {
        try {
            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(messageListener);
            this.connection.start();
            LOGGER.info("Queue connection has been started...");
        } catch (JMSException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    public void sendFailed(Message message) {
        LOGGER.warn("Send message failed: " + message);
        //send message back to queue
    }

    @Override
    public void close() throws Exception {
    }
}