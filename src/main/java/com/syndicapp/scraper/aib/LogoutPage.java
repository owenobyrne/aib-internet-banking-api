// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   LogoutPage.java

package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.syndicapp.scraper.FSSUserAgent;

public class LogoutPage extends FSSUserAgent
{

    public LogoutPage()
    {
    }

    public static HashMap<String, Object> click(String page)
        throws Exception
    {
        HashMap<String, Object> outputParams = new HashMap<String, Object>();
        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/logout.htm");
        String transactionToken = null;
        Pattern p = Pattern.compile("action=\"logout.htm\" onsubmit=\"return isClickEnabled\\(\\)\">\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
        for(Matcher m = p.matcher(page); m.find();)
            transactionToken = m.group(1);

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        HttpResponse response = httpclient.execute(httppost);
        org.apache.http.HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        log.debug(page);
        outputParams.put("page", page);
        return outputParams;
    }
}