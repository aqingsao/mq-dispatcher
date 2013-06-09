package com.thoughtworks.i1.mq.jms;

import com.thoughtworks.i1.commons.SystemException;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.*;

public class QueueReceiver implements AutoCloseable {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(QueueReceiver.class);
    private final Connection connection;
    private final Session session;
    private final Queue queue;
    private final MessageConsumer consumer;

    @Inject
    public QueueReceiver(Connection connection, Session session, Queue queue) {
        this.connection = connection;
        this.session = session;
        this.queue = queue;
        try {
            this.consumer = this.session.createConsumer(queue);
        } catch (JMSException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    public void start(MessageListener messageListener) {
        try {
            this.consumer.setMessageListener(messageListener);
            this.connection.start();
            LOGGER.info("Queue connection has been started...");
        } catch (JMSException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    public void sendFailed(Message message) {
        LOGGER.warn("Send message failed: " + message);
    }

    @Override
    public void close() throws Exception {
    }
}