// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TransferToAnotherROIAccountStep2.java

package com.syndicapp.scraper.aib;

import com.syndicapp.scraper.FSSUserAgent;
import com.syndicapp.scraper.aib.model.AccountDropdownItem;
import com.syndicapp.scraper.aib.model.AccountDropdownList;
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

public class TransferToAnotherROIAccountStep2 extends FSSUserAgent
{

    public TransferToAnotherROIAccountStep2()
    {
    }

    public static HashMap click(String page, HashMap inputParams)
        throws Exception
    {
        HashMap outputParams = new HashMap();
        String transactionToken = null;
        Pattern p = Pattern.compile("transactionToken\" value=\"(\\d+)\"/>\\s*<input type=\"hidden\" name=\"iBankFormSubmission\" value=\"true\" />\\s*<div>\\s*<input type=\"image\" class=\"aibRowRight\" name=\"_target2");
        for(Matcher m = p.matcher(page); m.find();)
            transactionToken = m.group(1);

        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/fundstransferroi.htm");
        List nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("paymentDetailsMethod", "0"));
        nvps.add(new BasicNameValuePair("iBankFormSubmission", "true"));
        nvps.add(new BasicNameValuePair("_target2.x", "21"));
        nvps.add(new BasicNameValuePair("_target2.y", "15"));
        log.debug((new StringBuilder()).append("Clicking 'Transfer to Another ROI Account Step 2' with ").append(nvps.toString()).toString());
        httppost.setEntity(new UrlEncodedFormEntity(nvps, "ISO-8859-1"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        if(!page.contains("Message to appear on receiver statement:"))
            throw new UnexpectedPageContentsException("Didn't get to the Transfer to Another ROI Account Step 2 Page!");
        p = Pattern.compile("receiverAccountIndex\" class=\"jsIntroTextA04 aibTextStyle06\" onchange=\"return setCrNarrative\\(\\)\">\\s*<option value=\"-1\" selected=\"selected\">Please select:</option>\\s*(.*?)\\s*</select>");
        AccountDropdownList addl = new AccountDropdownList();
        for(Matcher m = p.matcher(page); m.find();)
        {
            p = Pattern.compile("<option value=\"(\\d+)\">(.*?)</option>");
            m = p.matcher(m.group(1));
            while(m.find()) 
            {
                addl.addAccountDropdownItem(new AccountDropdownItem(m.group(1), m.group(2), ""));
                log.debug((new StringBuilder()).append(m.group(0)).append(": ").append(m.group(1)).append(" - ").append(m.group(2)).toString());
            }
        }

        outputParams.put("toAccounts", addl);
        outputParams.put("page", page);
        return outputParams;
    }
}