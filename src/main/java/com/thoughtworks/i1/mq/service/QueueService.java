package com.thoughtworks.i1.mq.service;

import javax.jms.Message;

// Used to retrieve message from 2 queues(1 messgae queue and 1 failure queue), and put failure message to another queue
public class QueueService {
    public void sendFailed(Message message) {
    }
}
