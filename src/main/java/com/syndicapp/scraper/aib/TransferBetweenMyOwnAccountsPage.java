// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TransferBetweenMyOwnAccountsPage.java

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

public class TransferBetweenMyOwnAccountsPage extends FSSUserAgent
{

    public TransferBetweenMyOwnAccountsPage()
    {
    }

    public static HashMap click(String page, HashMap inputParams)
        throws Exception
    {
        HashMap outputParams = new HashMap();
        String transactionToken = null;
        Pattern p = Pattern.compile("id=\"form2\" method=\"post\" action=\"transfersandpaymentslanding.htm\" onsubmit=\"return isClickEnabled\\(\\)\">\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
        Matcher m;
        for(m = p.matcher(page); m.find();)
            transactionToken = m.group(1);

        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/transfersandpaymentslanding.htm");
        List nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("iBankFormSubmission", "true"));
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("selectedPaymentType", "1"));
        Log.debug((new StringBuilder()).append("Clicking 'Transfers between my own accounts' with ").append(nvps.toString()).toString());
        httppost.setEntity(new UrlEncodedFormEntity(nvps, "ISO-8859-1"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        if(!page.contains("Transfer between my own AIB Accounts"))
            throw new UnexpectedPageContentsException("Didn't get to the Transfers between my own accounts Page!");
        p = Pattern.compile("selectedFromAccountIndex\" class=\"jsIntroText0 aibInputStyle04\" onchange=\"return setDrNarrative\\(\\)\">\\s*<option value=\"-1\" selected=\"selected\">Please select:</option>\\s*(.*?)\\s*</select>");
        for(m = p.matcher(page); m.find();)
        {
            p = Pattern.compile("<option value=\"(\\d+)\">(.*?) \\(([\\s\\d\\.,DCR]*?)\\)</option>");
            m = p.matcher(m.group(1));
            while(m.find()) 
                Log.debug((new StringBuilder()).append(m.group(0)).append(": ").append(m.group(1)).append(" - ").append(m.group(2)).append(" - ").append(m.group(3)).toString());
        }

        p = Pattern.compile("selectedToAccountIndex\" class=\"jsIntroText0 aibInputStyle04\">\\s*<option value=\"-1\" selected=\"selected\">Please select:</option>\\s*(.*?)\\s*</select>");
        m = p.matcher(page);
        if(m.find())
        {
            p = Pattern.compile("<option value=\"(\\d+)\">(.*?) \\(([\\s\\d\\.,DCR]*?)\\)</option>");
            for(m = p.matcher(m.group(1)); m.find(); Log.debug((new StringBuilder()).append(m.group(0)).append(": ").append(m.group(1)).append(" - ").append(m.group(2)).append(" - ").append(m.group(3)).toString()));
        }
        outputParams.put("page", page);
        return outputParams;
    }
}