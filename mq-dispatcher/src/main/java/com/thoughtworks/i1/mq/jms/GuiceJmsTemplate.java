package com.thoughtworks.i1.mq.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public interface GuiceJmsTemplate extends AutoCloseable {
    public void send(MessageCreator messageCreator) throws JMSException;
    public Message receive() throws JMSException;
}
