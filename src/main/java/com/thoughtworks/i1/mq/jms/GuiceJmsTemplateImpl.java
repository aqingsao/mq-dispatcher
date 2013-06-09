package com.thoughtworks.i1.mq.jms;

import com.google.inject.Inject;
import javax.jms.*;

public class GuiceJmsTemplateImpl implements GuiceJmsTemplate {

    private final Session session;
    private final Destination destination;
    private final Connection connection;

    @Inject
    public GuiceJmsTemplateImpl(Session session, Destination destination,Connection connection){
        this.session = session;
        this.destination = destination;
        this.connection=connection;
    }

    public void send(MessageCreator messageCreator) throws JMSException {
        Message message = messageCreator.createMessage(session);
        session.createProducer(destination).send(message);
    }

    public Message receive() throws JMSException {
        MessageConsumer consumer = session.createConsumer(destination);
        connection.start();
        return consumer.receive();
    }

    public void close() throws Exception {
        connection.close();
        session.close();
    }
}
