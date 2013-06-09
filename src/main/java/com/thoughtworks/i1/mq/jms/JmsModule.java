package com.thoughtworks.i1.mq.jms;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.thoughtworks.i1.commons.SystemException;
import com.thoughtworks.i1.commons.config.builder.Builder;

import javax.jms.*;

public class JmsModule extends AbstractModule {
    private final Connection connection;
    private final Session session;
    private String queueName;
    private final Queue queue;
    private QueueReceiver queueReceiver;

    private JmsModule(Connection connection, Session session, String queueName) {
        this.connection = connection;
        this.session = session;
        this.queueName = queueName;
        try {
            this.queue = session.createQueue(queueName);
        } catch (JMSException e) {
            throw new SystemException(e.getMessage(), e);
        }
        queueReceiver = new QueueReceiver(connection, session, queue);
    }

    protected void configure() {
        bind(QueueReceiver.class).annotatedWith(Names.named(queueName)).toInstance(queueReceiver);
    }

    public static class QueueBuilder implements Builder<JmsModule> {
        private ConnectionFactory connectionFactory;
        private String queueName;
        private boolean transacted = false;
        private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

        public QueueBuilder(ConnectionFactory connectionFactory, String queueName) {
            this.connectionFactory = connectionFactory;
            this.queueName = queueName;
        }

        @Override
        public JmsModule build() {
            try {
                Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(transacted, acknowledgeMode);

                return new JmsModule(connection, session, queueName);
            } catch (JMSException e) {
                throw new SystemException("Could not connect to destination " + queueName, e);
            }
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
