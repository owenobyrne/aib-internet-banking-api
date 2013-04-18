// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LogoutResource.java

package com.owenobyrne.aibibs.resource;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.owenobyrne.aibibs.services.AibInternetBankingService;
import com.owenobyrne.aibibs.services.CassandraService;

@Path("/logout")
@Component
public class LogoutResource
{
	@Context
    UriInfo uriInfo;
	@Context
    Request request;
    @Autowired
    AibInternetBankingService aibibs;
    @Autowired
    CassandraService cassandra;

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, Object> logout(MultivaluedMap<String, String> params)
    {
        String sessionId = params.getFirst("SESSION_ID");
        String page = cassandra.getData(CassandraService.CF_SESSIONS, sessionId, "page");
        if(page != null)
        {
            HashMap<String, Object> response = aibibs.logout(page);
            cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
            return response;
        } else
        {
            cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
            HashMap<String, Object> r = new HashMap<String, Object>();
            r.put("error", "Session has expired");
            return r;
        }
    }

	}