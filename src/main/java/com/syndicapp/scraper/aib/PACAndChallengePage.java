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
import com.syndicapp.scraper.exception.UnexpectedPageContentsException;

public class PACAndChallengePage extends FSSUserAgent {
	
    public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
        throws Exception {
    	String thisPage = "https://onlinebanking.aib.ie/inet/roi/login.htm";
    	
        HashMap<String, Object> outputParams = new HashMap<String, Object>();
        HttpPost httppost = new HttpPost(thisPage);
        
        String transactionToken = null;
        Pattern p = Pattern.compile("<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d*)\"/>");
        Matcher m = p.matcher(page);
        
        while (m.find()) {
            transactionToken = m.group(1);
        }

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("_finish", "true"));
        nvps.add(new BasicNameValuePair("jsEnabled", "TRUE"));
        nvps.add(new BasicNameValuePair("pacDetails.pacDigit1", (String)inputParams.get("pacDetails.pacDigit1")));
        nvps.add(new BasicNameValuePair("pacDetails.pacDigit2", (String)inputParams.get("pacDetails.pacDigit2")));
        nvps.add(new BasicNameValuePair("pacDetails.pacDigit3", (String)inputParams.get("pacDetails.pacDigit3")));
        
        httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        httppost.setHeader("Referer", PageUtils.getReferer(page));
        
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        
        log.trace(page);
        
        if(!page.contains("QuickPay Start")) {
            // check for action="accountoverview.htm" - we are probably on an info page that AIB display now and again.
        	
        	p = Pattern.compile("action=\"accountoverview.htm\"");
            m = p.matcher(page);
            
            if (m.find()) {
            	// looks like we're on an info page 
            	
                outputParams.put("page", thisPage + "\n" + page);
                outputParams.put("infoPage", true);
                
                return outputParams;
            
            } else {
            	// dunno what happened.            	
            	throw new UnexpectedPageContentsException("Didn't get to the Account Overview Page!");
            }

            
        } else {
            outputParams.put("page", thisPage + "\n" + page);
            //outputParams.put("infoPage", false);
            
            ArrayList<Account> accounts = PageUtils.parseBalances(page);
            outputParams.put("balances", accounts);
            
            return outputParams;
        }    
    }
    

}