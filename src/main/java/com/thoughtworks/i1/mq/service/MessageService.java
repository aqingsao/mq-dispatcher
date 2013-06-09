package com.thoughtworks.i1.mq.service;

import com.google.common.base.Optional;
import com.thoughtworks.i1.mq.jms.MessageWrapper;
import com.thoughtworks.i1.mq.jms.QueueReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MessageService implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    public static final String PROPERTY_DEVICE_ID = "deviceId";

    private DeviceService deviceService;
    private DispatchService dispatchService;
    private QueueReceiver queueReceiver;

    @Inject
    public MessageService(DeviceService deviceService, DispatchService dispatchService,
                          @Named("TEST.FOO") QueueReceiver queueReceiver) {
        this.deviceService = deviceService;
        this.dispatchService = dispatchService;
        this.queueReceiver = queueReceiver;
        this.queueReceiver.start(this);
    }

    @Override
    public void onMessage(Message message) {
        MessageWrapper messageWrapper = new MessageWrapper(message);
        LOGGER.info("Receive message: " + messageWrapper.getStringProperty(PROPERTY_DEVICE_ID));
        boolean result = sendMessageTo(messageWrapper);
        if (!result) {
            queueReceiver.sendFailed(message);
        }
    }

    private boolean sendMessageTo(MessageWrapper message) {
        boolean result = false;
        Optional<String> server = this.deviceService.getServer(message.getStringProperty(PROPERTY_DEVICE_ID));
        if (server.isPresent()) {
            result = dispatchService.sendMessage(message.getMessage(), server.get());
        }
        return result;
    }
}
