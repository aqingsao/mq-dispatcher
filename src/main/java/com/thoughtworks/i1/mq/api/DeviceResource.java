package com.thoughtworks.i1.mq.api;

import com.thoughtworks.i1.mq.service.DispatchService;
import org.apache.activemq.command.ActiveMQMessage;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("device")
public class DeviceResource {
    DispatchService dispatchService;

    @Inject
    public DeviceResource(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @Path("/{deviceId}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendMsg(@PathParam("deviceId") String deviceId, @FormParam("message") String msg) throws JMSException {
        Message message = new ActiveMQMessage();
        message.setStringProperty("deviceId", deviceId);
        message.setStringProperty("body", msg);
        boolean result = dispatchService.sendMessage(message);

        Response.Status created = result ? Response.Status.CREATED : Response.Status.INTERNAL_SERVER_ERROR;

        return Response.status(created).build();
    }
}
