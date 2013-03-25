// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TestResource.java

package com.owenobyrne.aibibs.resource;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

public class TestResource
{

    public TestResource()
    {
    }

    public String respondAsReady()
    {
        return "Demo service is ready!";
    }

    UriInfo uriInfo;
    Request request;
}