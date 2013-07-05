package com.thoughtworks.i1.mq.service;

import com.thoughtworks.i1.mq.jms.MessageWrapper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.Response;

public class MessageService implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    public static final String PROPERTY_DEVICE_ID = "deviceId";

    private DeviceService deviceService;
    private String endPoint;
    private HttpClient client;

    @Inject
    public MessageService(DeviceService deviceService, @Named("androidSendEndPoint")String endPoint) {
        this.deviceService = deviceService;
        this.endPoint = endPoint;
        client = new HttpClient();
    }

    @Override
    public void onMessage(Message message) {
        MessageWrapper messageWrapper = new MessageWrapper(message);
        LOGGER.info("Receive message: " + messageWrapper.getStringProperty(PROPERTY_DEVICE_ID));
        boolean result = sendMessageTo(messageWrapper);
        if (!result) {
            //send message back to queue
        }
    }

    private boolean sendMessageTo(MessageWrapper message) {
        boolean result = false;

        PostMethod method = new PostMethod(endPoint);
        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        method.addParameter("broadcast", "N");
        method.addParameter("username", message.getStringProperty("deviceId"));
        method.addParameter("title", "");
        method.addParameter("message", message.getStringProperty("body"));
        method.addParameter("uri", "");

        try {
            int code = client.executeMethod(method);
            if(code == Response.Status.OK.getStatusCode()) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
