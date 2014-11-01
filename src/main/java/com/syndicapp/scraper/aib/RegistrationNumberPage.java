// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RegistrationNumberPage.java

package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.syndicapp.scraper.FSSUserAgent;

public class RegistrationNumberPage extends FSSUserAgent
{

    public RegistrationNumberPage()
    {
    }

    public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
        throws Exception
    {
        HashMap<String, Object> outputParams = new HashMap<String, Object>();
        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/login.htm");
        String transactionToken = null;
        Pattern p = Pattern.compile("<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d*)\"/>");
        Matcher m;
        for(m = p.matcher(page); m.find();)
            transactionToken = m.group(1);

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("regNumber", (String)inputParams.get("regNumber")));
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("jsEnabled", "TRUE"));
        nvps.add(new BasicNameValuePair("_target1", "true"));
        httppost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        log.debug(page);
        outputParams.put("page", page);
        p = Pattern.compile("<label for=\"digit\\d\"><strong>Digit (\\d)</strong></label>");
        m = p.matcher(page);
        for(int d = 1; m.find(); d++)
            outputParams.put((new StringBuilder()).append("digit").append(d).toString(), m.group(1));

        p = Pattern.compile("Please enter the <strong>([^<]*)</strong> from your <strong>([^<]*)</strong>");
        for(m = p.matcher(page); m.find(); outputParams.put("whatvalue", m.group(2)))
            outputParams.put("howmuch", m.group(1));

        return outputParams;
    }
}