package com.thoughtworks.i1.mq.service;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private Map<String, List<String>> server2DeviceIds;

    public DeviceService() {
        server2DeviceIds = new HashMap();
    }

    // To declare that a device has connected to a server
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

    // To declare that a device has disconnected from a server
    public void onDisconnected(String server, String deviceId) {
        LOGGER.info("Will disconnect deviceId {1} from server {2}", deviceId, server);
        if (!server2DeviceIds.containsKey(server)) {
            LOGGER.info("Cannot find server {1}", server);
            return;
        }
        boolean remove = server2DeviceIds.get(server).remove(deviceId);
        if (!remove) {
            LOGGER.info("Server {1} does not contain deviceId {2}", server, deviceId);
        } else {
            LOGGER.info("DeviceId {1} has been disconnected from server {2}", deviceId, server);
        }
    }

    public List<String> getDeviceIds(String server) {
        if (!server2DeviceIds.containsKey(server)) {
            return Collections.EMPTY_LIST;
        }
        return server2DeviceIds.get(server);
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
