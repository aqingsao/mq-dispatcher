package com.thoughtworks.i1.mq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;

public class DispatcherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherService.class);

    public void onConnected(String uri, String deviceId) {
        LOGGER.info("Device {1} connected to server {2}", deviceId, uri);

    }

    public void onMessage(Message message) {
        LOGGER.info("Receive message {1}", message);
    }
}
