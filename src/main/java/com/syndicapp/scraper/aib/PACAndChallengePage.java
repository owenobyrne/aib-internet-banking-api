// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PACAndChallengePage.java

package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.syndicapp.scraper.FSSUserAgent;
import com.syndicapp.scraper.aib.model.Account;

public class PACAndChallengePage extends FSSUserAgent
{

    public PACAndChallengePage()
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
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("_finish", "true"));
        nvps.add(new BasicNameValuePair("jsEnabled", "TRUE"));
        nvps.add(new BasicNameValuePair("pacDetails.pacDigit1", (String)inputParams.get("pacDetails.pacDigit1")));
        nvps.add(new BasicNameValuePair("pacDetails.pacDigit2", (String)inputParams.get("pacDetails.pacDigit2")));
        nvps.add(new BasicNameValuePair("pacDetails.pacDigit3", (String)inputParams.get("pacDetails.pacDigit3")));
        nvps.add(new BasicNameValuePair("challengeDetails.challengeEntered", (String)inputParams.get("challengeDetails.challengeEntered")));
        httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        outputParams.put("page", page);
        p = Pattern.compile("<span>([\\s\\w\\d]+\\-\\d{3,4})</span>\\s*</button>\\s*</form>.*?<h3>([\\d,.]+)\\s([DR]*)\\s*[&<]", 32);
        m = p.matcher(page);
        ArrayList<Account> accounts = new ArrayList<Account>();
        for(int i = 0; m.find(); i++)
        {
            Account a = new Account(i, m.group(1), m.group(2), m.group(3));
            accounts.add(a);
        }

        outputParams.put("balances", accounts);
        return outputParams;
    }
}