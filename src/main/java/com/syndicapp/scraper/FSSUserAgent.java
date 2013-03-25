// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   FSSUserAgent.java

package com.syndicapp.scraper;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.log4j.Logger;

public class FSSUserAgent
{

    public FSSUserAgent()
    {
    }

    public static DefaultHttpClient httpclient = new DefaultHttpClient();
    public static Logger Log = Logger.getLogger(FSSUserAgent.class);
    public static BasicHttpContext context = new BasicHttpContext();

}