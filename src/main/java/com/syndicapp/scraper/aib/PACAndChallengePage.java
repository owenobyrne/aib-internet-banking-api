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

public class PACAndChallengePage extends FSSUserAgent {
	
    public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
        throws Exception {
    	
        HashMap<String, Object> outputParams = new HashMap<String, Object>();
        HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/login.htm");
        
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
        nvps.add(new BasicNameValuePair("challengeDetails.challengeEntered", (String)inputParams.get("challengeDetails.challengeEntered")));
        
        httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        page = EntityUtils.toString(entity);
        
        outputParams.put("page", page);
        //System.out.println(page);
        
        // regex yeeeehaw!!! What a bitch this was to figure out!
        p = Pattern.compile("<span>([\\s\\w\\d]+\\-\\d{3,4})</span>\\s*</button>\\s*</form>(?:(?!transactionToken).)*?<h3>\\s*([\\d,.]+)\\s(DR)?\\s*[&<]((?:(?!transactionToken).)*?Available Funds.*?([\\d,.]+)\\s?(DR)?&)?", Pattern.DOTALL);
        m = p.matcher(page);
        
        ArrayList<Account> accounts = new ArrayList<Account>();

        int i = 0; 
        while(m.find()) {
        	Account a = null;
        	if (m.groupCount() == 6) {
        		if (null == m.group(5)) {
        			a = new Account(i, m.group(1), m.group(2), m.group(3));
        			System.out.println("Found account " + m.group(1));
        		} else {
        			a = new Account(i, m.group(1), m.group(5), m.group(6));
        			System.out.println("Found account with available balance " + m.group(1));
        		}
        	}
            accounts.add(a);
        	i++;
        }

        outputParams.put("balances", accounts);
        return outputParams;
    }
}