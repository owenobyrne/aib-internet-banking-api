package com.owenobyrne.aibibs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/test")
public class TestResource
{
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady()
    {
        return "Demo service is ready!";
    }


}