// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LoginResource.java

package com.owenobyrne.aibibs.resource;

import java.util.HashMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.eaio.uuid.UUID;
import com.owenobyrne.aibibs.services.AibInternetBankingService;
import com.owenobyrne.aibibs.services.CassandraService;

public class LoginResource
{

    public LoginResource()
    {
    }

    public HashMap<String, Object> enterRegistrationNumber(MultivaluedMap<String, String> registrationParams)
    {
        String registrationNumber = registrationParams.getFirst("REGISTRATION_NUMBER");
        HashMap<String, Object> response = aibibs.enterRegistrationNumber(registrationNumber);
        UUID sessionId = new UUID();
        cassandra.addData(CassandraService.CF_SESSIONS, sessionId.toString(), "page", (String)response.get("page"), 180);
        response.put("sessionId", sessionId.toString());
        response.remove("page");
        return response;
    }

    public HashMap<String, Object> enterPACDigits(MultivaluedMap<String, String> pacParams)
    {
        String pac1 = (String)pacParams.getFirst("PAC1");
        String pac2 = (String)pacParams.getFirst("PAC2");
        String pac3 = (String)pacParams.getFirst("PAC3");
        String digits = (String)pacParams.getFirst("DIGITS");
        String sessionId = (String)pacParams.getFirst("SESSION_ID");
        String page = cassandra.getData(CassandraService.CF_SESSIONS, sessionId, "page");
        if(page != null)
        {
            HashMap<String, Object> response = aibibs.enterPACDigits(page, pac1, pac2, pac3, digits);
            cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page", (String)response.get("page"), 180);
            response.put("sessionId", sessionId);
            response.remove("page");
            return response;
        } else
        {
            cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
            HashMap<String, Object> r = new HashMap<String, Object>();
            r.put("error", "Session has expired");
            return r;
        }
    }

    UriInfo uriInfo;
    Request request;
    AibInternetBankingService aibibs;
    CassandraService cassandra;
}