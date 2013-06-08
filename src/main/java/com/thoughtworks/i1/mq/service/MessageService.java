package com.thoughtworks.i1.mq.service;

import com.google.common.base.Optional;
import com.thoughtworks.i1.commons.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;

public class MessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    public static final String PROPERTY_DEVICE_ID = "deviceId";

    private DeviceService deviceService;
    private DispatchService dispatchService;
    private QueueService queueService;

    @Inject
    public MessageService(DeviceService deviceService, DispatchService dispatchService, QueueService queueService) {
        this.deviceService = deviceService;
        this.dispatchService = dispatchService;
        this.queueService = queueService;
    }

    public void onMessage(Message message) {
        boolean result = sendMessageTo(message);
        if (!result) {
            queueService.sendFailed(message);
        }
    }

    private boolean sendMessageTo(Message message) {
        boolean result = false;
        try {
            Optional<String> server = this.deviceService.getServer(message.getStringProperty(PROPERTY_DEVICE_ID));
            if (server.isPresent()) {
                result = dispatchService.sendMessage(message, server.get());
            }
        } catch (JMSException e) {
            LOGGER.warn("Failed to send message: " + e.getMessage(), e);
        }
        return result;
    }
}