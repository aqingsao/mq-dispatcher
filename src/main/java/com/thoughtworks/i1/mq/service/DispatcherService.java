package com.thoughtworks.i1.mq.service;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import java.util.*;

public class DispatcherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherService.class);
    private Map<String, List<String>> server2DeviceIds;

    public DispatcherService() {
        server2DeviceIds = new HashMap();
    }

    public void onConnected(String server, String deviceId) {
        LOGGER.info("Device {1} connected to server {2}", deviceId, server);
        for (Map.Entry<String, List<String>> entry : server2DeviceIds.entrySet()) {
            List<String> deviceIds = entry.getValue();
            if (deviceIds.contains(deviceId)) {
                deviceIds.remove(deviceId);
                LOGGER.info("Will remove device {1} from server {2}", deviceId, server);
            }
        }
        List<String> deviceIds = server2DeviceIds.get(server);
        if (deviceIds == null) {
            deviceIds = new ArrayList<String>();
            server2DeviceIds.put(server, deviceIds);
        }
        deviceIds.add(deviceId);
        LOGGER.info("Add device {1} from server {2}", deviceId, server);
    }

    public List<String> getDeviceIds(String server) {
        if (!server2DeviceIds.containsKey(server)) {
            return Collections.EMPTY_LIST;
        }
        return server2DeviceIds.get(server);
    }

    public void onMessage(Message message) {
        LOGGER.info("Receive message {1}", message);
    }

    public Optional<String> getServer(String deviceId) {
        for (Map.Entry<String, List<String>> entry : server2DeviceIds.entrySet()) {
            List<String> deviceIds = entry.getValue();
            if (deviceIds.contains(deviceId)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.absent();
    }
}
