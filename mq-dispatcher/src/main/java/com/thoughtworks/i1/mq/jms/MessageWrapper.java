package com.thoughtworks.i1.mq.jms;

import com.thoughtworks.i1.commons.SystemException;

import javax.jms.JMSException;
import javax.jms.Message;

public class MessageWrapper{
    private Message message;

    public MessageWrapper(Message message) {
        this.message = message;
    }

    public String getStringProperty(String name) {
        try {
            return message.getStringProperty(name);
        } catch (JMSException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    public Message getMessage() {
        return message;
    }
}
