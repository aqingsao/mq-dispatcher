package com.thoughtworks.i1.mq.service;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DeviceServiceTest {

    private DeviceService deviceService;

    @Before
    public void before() {
        deviceService = new DeviceService();
    }

    @Test
    public void getDeviceIds_should_return_empty_when_no_deviceIds_connect_to_a_server() throws Exception {
        List<String> deviceIds = deviceService.getDeviceIds("1");
        assertThat(deviceIds.isEmpty(), is(true));
    }

    @Test
    public void getDeviceIds_should_return_deviceIds_when_at_least_1_deviceIds_connect_to_a_server() throws Exception {
        deviceService.onConnected("1", "11");
        List<String> deviceIds = deviceService.getDeviceIds("1");
        assertThat(deviceIds.size(), is(1));
        assertThat(deviceIds.get(0), is("11"));
    }

    @Test
    public void getServer_should_return_absent_when_device_11_does_not_connect_to_a_server() throws Exception {
        Optional<String> actual = deviceService.getServer("11");
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void getServer_should_return_1_when_device_11_connected_to_server_1() throws Exception {
        deviceService.onConnected("1", "11");
        Optional<String> actual = deviceService.getServer("11");
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("1"));
    }

    @Test
    public void onConnected_should_remove_from_previous_server_when_device_connected_to_new_server() {
        deviceService.onConnected("1", "11");
        deviceService.onConnected("2", "11");
        assertThat(deviceService.getDeviceIds("1").isEmpty(), is(true));
        assertThat(deviceService.getDeviceIds("2").size(), is(1));
        assertThat(deviceService.getDeviceIds("2").get(0), is("11"));
    }

    @Test
    public void onConnected_should_add_more_than_1_deviceIds_to_a_server() {
        deviceService.onConnected("1", "11");
        deviceService.onConnected("1", "12");
        assertThat(deviceService.getDeviceIds("1").size(), is(2));
        assertThat(deviceService.getDeviceIds("1").get(0), is("11"));
        assertThat(deviceService.getDeviceIds("1").get(1), is("12"));
    }

    @Test
    public void onDisConnected_should_remove_deviceId_from_a_server() {
        deviceService.onConnected("1", "11");
        deviceService.onDisconnected("1", "11");
        assertThat(deviceService.getDeviceIds("1").contains("11"), is(false));
    }

    @Test
    public void onDisConnected_should_do_nothing_when_deviceId_is_not_connected_to_a_server() {
        deviceService.onConnected("1", "12");
        deviceService.onDisconnected("1", "11");
        assertThat(deviceService.getDeviceIds("1").contains("11"), is(false));
    }

    @Test
    public void onDisConnected_should_do_nothing_when_a_specific_server_does_not_exist() {
        deviceService.onDisconnected("1", "11");
        assertThat(deviceService.getDeviceIds("1").contains("11"), is(false));
    }

    @Test
    public void test() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("test.xml");
    }
}
