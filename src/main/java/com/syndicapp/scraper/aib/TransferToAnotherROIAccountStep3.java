// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TransferToAnotherROIAccountStep3.java

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

public class TransferToAnotherROIAccountStep3 extends FSSUserAgent
{

    public TransferToAnotherROIAccountStep3()
    {
    }

    public static HashMap click(String page, HashMap inputParams)
        throws Exception
    {
        HashMap outputParams = new HashMap();
        String transactionToken = null;
        Pattern p = Pattern.compile("transactionToken\" value=\"(\\d+)\"/>\\s*<input type=\"hidden\" name=\"iBankFormSubmission\" value=\"true\" />\\s*<div>\\s*<input type=\"image\" class=\"aibRowRight\" name=\"_target6");
        for(Matcher m = p.matcher(page); m.find();)
            transactionToken = m.group(1);

        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/fundstransferroi.htm");
        List nvps = new ArrayList();
        p = Pattern.compile("<input type=\"hidden\" name=\"accountRefList\" value=\"\"/>");
        for(Matcher m = p.matcher(page); m.find(); nvps.add(new BasicNameValuePair("accountRefList", "")));
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("iBankFormSubmission", "true"));
        nvps.add(new BasicNameValuePair("receiverAccountIndex", (String)inputParams.get("toAccountId")));
        nvps.add(new BasicNameValuePair("receiverReference", (String)inputParams.get("receiverReference")));
        nvps.add(new BasicNameValuePair("paymentAmount.euro", (String)inputParams.get("amounteuro")));
        nvps.add(new BasicNameValuePair("paymentAmount.cent", (String)inputParams.get("amountcent")));
        nvps.add(new BasicNameValuePair("_target6.x", "21"));
        nvps.add(new BasicNameValuePair("_target6.y", "15"));
        log.debug((new StringBuilder()).append("Clicking 'Transfer to Another ROI Account Step 3' with ").append(nvps.toString()).toString());
        httppost.setEntity(new UrlEncodedFormEntity(nvps, "ISO-8859-1"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        if(!page.contains("You have requested to make the following transfer:"))
            throw new UnexpectedPageContentsException("Didn't get to the Transfer to Another ROI Account Step 3 Page!");
        String digitRequested = null;
        p = Pattern.compile("<label for=\"digit\"><strong>Digit (\\d)</strong>");
        for(Matcher m = p.matcher(page); m.find();)
            digitRequested = m.group(1);

        outputParams.put("digitRequested", digitRequested);
        outputParams.put("page", page);
        return outputParams;
    }
}