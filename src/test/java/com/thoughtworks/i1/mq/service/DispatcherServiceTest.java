package com.thoughtworks.i1.mq.service;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DispatcherServiceTest {

    private DispatcherService dispatcherService;

    @Before
    public void before() {
        dispatcherService = new DispatcherService();
    }

    @Test
    public void getDeviceIds_should_return_empty_when_no_deviceIds_connect_to_a_server() throws Exception {
        List<String> deviceIds = dispatcherService.getDeviceIds("1");
        assertThat(deviceIds.isEmpty(), is(true));
    }

    @Test
    public void getDeviceIds_should_return_deviceIds_when_at_least_1_deviceIds_connect_to_a_server() throws Exception {
        dispatcherService.onConnected("1", "11");
        List<String> deviceIds = dispatcherService.getDeviceIds("1");
        assertThat(deviceIds.size(), is(1));
        assertThat(deviceIds.get(0), is("11"));
    }

    @Test
    public void getServer_should_return_absent_when_device_11_does_not_connect_to_a_server() throws Exception {
        Optional<String> actual = dispatcherService.getServer("11");
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    public void getServer_should_return_1_when_device_11_connected_to_server_1() throws Exception {
        dispatcherService.onConnected("1", "11");
        Optional<String> actual = dispatcherService.getServer("11");
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is("1"));
    }

    @Test
    public void onConnected_should_remove_from_previous_server_when_device_connected_to_new_server() {
        dispatcherService.onConnected("1", "11");
        dispatcherService.onConnected("2", "11");
        assertThat(dispatcherService.getDeviceIds("1").isEmpty(), is(true));
        assertThat(dispatcherService.getDeviceIds("2").size(), is(1));
        assertThat(dispatcherService.getDeviceIds("2").get(0), is("11"));
    }

    @Test
    public void onConnected_should_add_more_than_1_deviceIds_to_a_server() {
        dispatcherService.onConnected("1", "11");
        dispatcherService.onConnected("1", "12");
        assertThat(dispatcherService.getDeviceIds("1").size(), is(2));
        assertThat(dispatcherService.getDeviceIds("1").get(0), is("11"));
        assertThat(dispatcherService.getDeviceIds("1").get(1), is("12"));
    }
}
