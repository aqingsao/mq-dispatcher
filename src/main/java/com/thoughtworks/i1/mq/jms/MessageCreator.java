package com.thoughtworks.i1.mq.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public interface MessageCreator {
    public Message createMessage(Session session) throws JMSException; 
}
