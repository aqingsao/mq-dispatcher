package com.thoughtworks.i1.mq.jms;

import com.thoughtworks.i1.commons.SystemException;
import com.thoughtworks.i1.commons.config.builder.Builder;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class QueueReceiver implements AutoCloseable {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(QueueReceiver.class);
    private final Connection connection;
    private final Queue queue;
    private final Session session;

    public QueueReceiver(ConnectionFactory connectionFactory, String queueName, boolean transacted, int acknowledgeMode) {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(transacted, acknowledgeMode);
            queue = session.createQueue(queueName);
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
    }

    @Override
    public void close() throws Exception {
    }

    public static class QueueBuilder implements Builder<QueueReceiver> {
        private ConnectionFactory connectionFactory;
        private String queueName;
        private boolean transacted = false;
        private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

        public QueueBuilder(ConnectionFactory connectionFactory, String queueName) {
            this.connectionFactory = connectionFactory;
            this.queueName = queueName;
        }

        @Override
        public QueueReceiver build() {
            return new QueueReceiver(connectionFactory, queueName, transacted, acknowledgeMode);
        }

        public QueueBuilder transacted() {
            this.transacted = true; // will ignore acknowledge mode when transacted is true
            return this;
        }

        public QueueBuilder withAcknowledgeMode(int acknowledgeMode) {
            this.acknowledgeMode = acknowledgeMode;
            return this;
        }
    }
}