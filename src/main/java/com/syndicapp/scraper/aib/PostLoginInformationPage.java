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

public class PostLoginInformationPage extends FSSUserAgent {
	
    public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
        throws Exception {
    	
        HashMap<String, Object> outputParams = new HashMap<String, Object>();
        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/accountoverview.htm");
        
        String transactionToken = null;
        // attempt to find the transactionToken - might move around each time they create an info page..
        Pattern p = Pattern.compile("action=\"accountoverview.htm\".*?<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d*)\"/>");
        Matcher m = p.matcher(page);
        
        while (m.find()) {
            transactionToken = m.group(1);
        }

        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
        nvps.add(new BasicNameValuePair("iBankFormSubmission", "false"));
        nvps.add(new BasicNameValuePair("x", "189"));
        nvps.add(new BasicNameValuePair("y", "3"));
        
        httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        
        if(!page.contains("You are securely logged in.")) {
        	throw new UnexpectedPageContentsException("Didn't get to the Account Overview Page!");
                        
        } else {
            outputParams.put("page", page);
            
            ArrayList<Account> accounts = PageUtils.parseBalances(page);
            outputParams.put("balances", accounts);
            
            return outputParams;
        }    
    }
    

}