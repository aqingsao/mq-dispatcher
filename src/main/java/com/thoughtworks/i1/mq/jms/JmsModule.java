package com.thoughtworks.i1.mq.jms;

import com.google.inject.AbstractModule;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsModule extends AbstractModule {
    private final Destination destination;
    private final Session session;
    private final Connection connection;

    private JmsModule(Session session, Destination destination, Connection connection) {
        this.destination=destination;
        this.session=session;
        this.connection=connection;
    }

    protected void configure() {
        bind(Destination.class).toInstance(destination);
        bind(Session.class).toInstance(session);
        bind(Connection.class).toInstance(connection);
        bind(GuiceJmsTemplate.class).to(GuiceJmsTemplateImpl.class);
    }

    public static class Builder {
        private String queue;
        private Context context = null;
        private MessageListener listener;
        private ConnectionFactory connectionFactory;
        private boolean usingJNDI=false;

        public JmsModule buildModule() {
            try {
                Session session;
                Destination destination;
                Connection connection;
                if(usingJNDI) {
                    if(context==null)
                        context = new InitialContext();
                    connectionFactory= (ConnectionFactory) context.lookup("ConnectionFactory");
                    connection = connectionFactory.createConnection();
                    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                    destination = (Destination) context.lookup(this.queue);
                } else {
                    connection=connectionFactory.createConnection();
                    session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                    destination = session.createQueue(queue);
                }

                if(listener!=null) {
                    MessageConsumer consumer = session.createConsumer(destination);
                    consumer.setMessageListener(listener);
                    connection.start();
                }

                return new JmsModule(session,destination,connection);
            }
            catch(JMSException e) {
                throw new RuntimeException("Could not connect to destination",e);
            }
            catch(NamingException e) {
                throw new RuntimeException("Could not create initial context",e);
            }
        }
        public Builder usingJNDI(){
            this.usingJNDI=true;
            return this;
        }

        public Builder queue(String queue) {
            this.queue=queue;
            return this;
        }
        public Builder context(Context context){
            this.context = context;
            return this;
        }
        public Builder withListener(MessageListener listener) {
            this.listener = listener;
            return this;
        }
        public Builder withConnectionFactory(ConnectionFactory cf){
            this.connectionFactory = cf;

            return this;
        }
    }
}
