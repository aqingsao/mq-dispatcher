package com.thoughtworks.i1.mq.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("device")
public class DeviceResource {

//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("devices")
//    public List<JobVO> getQuartzJobs() {
//        return jobService.findAllJobs();
//    }
//
//    @POST
//    @Path("item")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response createJob(JobVO jobVO) {
//        try {
//            jobVO = jobService.createJob(jobVO);
//            URI path = context.getBaseUriBuilder().path(JobsResource.class).path("items").build();
//            return Response.created(path).entity(jobVO).build();
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            return Response.serverError().build();
//        }
//    }

}
