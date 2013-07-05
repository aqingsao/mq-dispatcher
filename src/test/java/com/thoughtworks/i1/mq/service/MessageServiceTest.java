package com.thoughtworks.i1.mq.service;

import com.google.common.base.Optional;
import com.thoughtworks.i1.mq.jms.QueueReceiver;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Message;

import static org.mockito.Mockito.*;

public class MessageServiceTest {

    private MessageService messageService;
    private DeviceService deviceService;
    private DispatchService dispatchService;
    private QueueReceiver queueReceiver;

    @Before
    public void before() {
        deviceService = mock(DeviceService.class);
        dispatchService = mock(DispatchService.class);
        queueReceiver = mock(QueueReceiver.class);

        messageService = new MessageService(deviceService, "xxx");
    }

    @Test
    public void should_dispatch_message_11_to_server_1() {
        String deviceId = "1";
        String server = "11";
        when(deviceService.getServer(deviceId)).thenReturn(Optional.of(server));
        ActiveMQTextMessage message = aTextMessage("a sample message", deviceId);
        when(dispatchService.sendMessage(message)).thenReturn(true);

        messageService.onMessage(message);

        verify(dispatchService, times(1)).sendMessage(message);
        verify(queueReceiver, times(0)).sendFailed(message);
    }

    @Test
    public void should_put_message_to_error_queue_when_failed_to_dispatch_message_11_to_server_1() {
        String deviceId = "1";
        String server = "11";
        when(deviceService.getServer(deviceId)).thenReturn(Optional.of(server));
        ActiveMQTextMessage message = aTextMessage("a sample message", deviceId);
        when(dispatchService.sendMessage(message)).thenReturn(false);

        messageService.onMessage(message);

        verify(queueReceiver, times(1)).sendFailed(message);
    }

    @Test
    public void should_put_message_to_error_queue_when_a_server_does_not_exist() {
        String deviceId = "1";
        when(deviceService.getServer(deviceId)).thenReturn(Optional.<String>absent());
        ActiveMQTextMessage message = aTextMessage("a sample message", deviceId);

        messageService.onMessage(message);

        verify(dispatchService, times(0)).sendMessage(any(Message.class));
        verify(queueReceiver, times(1)).sendFailed(message);
    }

    private ActiveMQTextMessage aTextMessage(String text, String deviceId) {
        try {
            ActiveMQTextMessage message = new ActiveMQTextMessage();
            message.setText(text);
            message.setStringProperty(MessageService.PROPERTY_DEVICE_ID, deviceId);
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
