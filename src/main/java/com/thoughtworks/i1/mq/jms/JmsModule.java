package com.thoughtworks.i1.mq.jms;

import com.google.inject.AbstractModule;
import com.thoughtworks.i1.commons.SystemException;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

public class JmsModule extends AbstractModule {
    private final Destination destination;
    private final Session session;
    private final Connection connection;

    private JmsModule(Connection connection, Session session, Destination destination) {
        this.destination = destination;
        this.session = session;
        this.connection = connection;
    }

    protected void configure() {
        bind(Destination.class).toInstance(destination);
        bind(Session.class).toInstance(session);
        bind(Connection.class).toInstance(connection);
        bind(GuiceJmsTemplate.class).to(GuiceJmsTemplateImpl.class);
    }

    public static class Builder {
        private ConnectionFactory connectionFactory;
        private String queue;
        private boolean transacted = false;
        private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

        public Builder(ConnectionFactory connectionFactory, String queue) {
            this.connectionFactory = connectionFactory;
            this.queue = queue;
        }

        public JmsModule buildModule() {
            try {
                Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(transacted, acknowledgeMode);
                Destination destination = session.createQueue(queue);

                connection.start();
                return new JmsModule(connection, session, destination);
            } catch (Exception e) {
                throw new SystemException("Could not connect to destination " + queue, e);
            }
        }

        public Builder transacted() {
            this.transacted = true; // will ignore acknowledge mode when transacted is true
            return this;
        }

        public Builder withAcknowledgeMode(int acknowledgeMode){
            this.acknowledgeMode = acknowledgeMode;
            return this;
        }
    }
}
