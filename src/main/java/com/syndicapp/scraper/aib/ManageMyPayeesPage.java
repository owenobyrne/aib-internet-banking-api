// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ManageMyPayeesPage.java

package com.syndicapp.scraper.aib;

import com.syndicapp.scraper.FSSUserAgent;
import com.syndicapp.scraper.aib.model.Payee;
import com.syndicapp.scraper.aib.model.PayeeList;
import com.syndicapp.scraper.exception.UnexpectedPageContentsException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class ManageMyPayeesPage extends FSSUserAgent
{

    public ManageMyPayeesPage()
    {
    }

    public static HashMap click(String page, HashMap inputParams)
        throws Exception
    {
        HashMap outputParams = new HashMap();
        String transactionToken = null;
        Pattern p = Pattern.compile("managemypayees.htm\" method=\"post\" ><input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
        for(Matcher m = p.matcher(page); m.find();)
            transactionToken = m.group(1);

        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/managemypayees.htm");
        List nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("isFormButtonClicked", "true"));
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("tabName", "Manage My Payees"));
        log.debug((new StringBuilder()).append("Clicking 'Transfers and Payments' with ").append(nvps.toString()).toString());
        httppost.setEntity(new UrlEncodedFormEntity(nvps, "ISO-8859-1"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        if(!page.contains("Add new payee"))
            throw new UnexpectedPageContentsException("Didn't get to the Manage My Payees Page!");
        p = Pattern.compile("<td>(.*?)</td>\\s*<td>Personal payment</td>\\s*<td></td>\\s*<td>(.*?)</td>\\s*<td>(.*?)</td>");
        PayeeList pl = new PayeeList();
        for(Matcher m = p.matcher(page); m.find(); pl.addPayee(new Payee(m.group(1), m.group(3), m.group(2))));
        outputParams.put("payees", pl);
        outputParams.put("page", page);
        return outputParams;
    }
}