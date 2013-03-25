// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LoginPage.java

package com.syndicapp.scraper.aib;

import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.syndicapp.scraper.FSSUserAgent;

public class LoginPage extends FSSUserAgent
{

    public LoginPage()
    {
    }

    public static HashMap<String, Object> click()
        throws Exception
    {
        HashMap<String, Object> bundle = new HashMap<String, Object>();
        HttpGet httpget = new HttpGet("https://aibinternetbanking.aib.ie/inet/roi/login.htm");
        HttpResponse response = httpclient.execute(httpget);
        org.apache.http.HttpEntity entity = response.getEntity();
        String page = EntityUtils.toString(entity);
        Log.debug(page);
        bundle.put("page", page);
        return bundle;
    }
}