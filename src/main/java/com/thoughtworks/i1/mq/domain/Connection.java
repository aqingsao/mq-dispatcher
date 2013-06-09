package com.thoughtworks.i1.mq.domain;

public class Connection {
    private String server;
    private String deviceId;

    public Connection(String server, String deviceId) {
        this.server = server;
        this.deviceId = deviceId;
    }

    public String getServer() {
        return server;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
