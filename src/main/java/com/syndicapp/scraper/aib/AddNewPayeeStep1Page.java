// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AddNewPayeeStep1Page.java

package com.syndicapp.scraper.aib;

import com.syndicapp.scraper.FSSUserAgent;
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

public class AddNewPayeeStep1Page extends FSSUserAgent
{

    public AddNewPayeeStep1Page()
    {
    }

    public static HashMap click(String page, HashMap inputParams)
        throws Exception
    {
        HashMap outputParams = new HashMap();
        String transactionToken = null;
        Pattern p = Pattern.compile("transactionToken\" value=\"(\\d+)\"/>\\s*<input type=\"hidden\" name=\"iBankFormSubmission\" value=\"true\" />\\s*<input id=\"nextButton");
        for(Matcher m = p.matcher(page); m.find();)
            transactionToken = m.group(1);

        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/addpayee.htm");
        List nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("sFormButtonClicked", "false"));
        nvps.add(new BasicNameValuePair("paymentType", "1"));
        nvps.add(new BasicNameValuePair("personalPayeeName", (String)inputParams.get("payeename")));
        nvps.add(new BasicNameValuePair("payeeNSC", (String)inputParams.get("payeensc")));
        nvps.add(new BasicNameValuePair("payeeAccountNumber", (String)inputParams.get("payeeaccountnum")));
        nvps.add(new BasicNameValuePair("iBankFormSubmission", "true"));
        nvps.add(new BasicNameValuePair("_target1.x", "43"));
        nvps.add(new BasicNameValuePair("_target1.y", "15"));
        log.debug((new StringBuilder()).append("Clicking 'Add new Payee Step 1' with ").append(nvps.toString()).toString());
        httppost.setEntity(new UrlEncodedFormEntity(nvps, "ISO-8859-1"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        if(!page.contains("You have requested to add the following payee"))
            throw new UnexpectedPageContentsException("Didn't get to the Add New Payees Confirm Page!");
        String codeRequested = null;
        p = Pattern.compile("<label for=\"code\"><strong>Code number (\\d{1,3})</strong>");
        for(Matcher m = p.matcher(page); m.find();)
            codeRequested = m.group(1);

        outputParams.put("codeRequested", codeRequested);
        outputParams.put("page", page);
        return outputParams;
    }
}