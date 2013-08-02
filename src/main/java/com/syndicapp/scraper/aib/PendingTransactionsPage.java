// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   StatementPage.java

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
import com.syndicapp.scraper.aib.model.AccountDropdownItem;
import com.syndicapp.scraper.aib.model.AccountDropdownList;
import com.syndicapp.scraper.aib.model.PendingTransaction;
import com.syndicapp.scraper.aib.model.PendingTransactionList;

public class PendingTransactionsPage extends FSSUserAgent {

	public PendingTransactionsPage() {
	}

	public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
			throws Exception {
		HashMap<String, Object> outputParams = new HashMap<String, Object>();
		HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/pendingtransactions.htm");
		log.trace(page);
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		Pattern p = null;
		String transactionToken = null;
		Matcher m;
		
		if (inputParams.get("index") != null) {
			// I've just clicked the pending transactions tab and 
			// now I want to select the account specified by "index"
	
			nvps.add(new BasicNameValuePair("index", (String) inputParams.get("index")));
			log.debug("Clicked the drop down");
			p = Pattern
					.compile("<label>Change account:\\s*<select id=\"index\" name=\"index\">\\s*(.*?)\\s*</select>\\s*</label>\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
			m = p.matcher(page);
			if (m.find())
				transactionToken = m.group(2);
			nvps.add(new BasicNameValuePair("isFormButtonClicked", "true"));
			nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
			
		} else {
			// I'm on the Recent Transactions page and I'm clicking the Pending Transactions tab.
			log.debug("Clicked the Pending Transactions Tab");
			p = Pattern
					.compile("action=\"pendingtransactions.htm\" method=\"post\" ><input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
			m = p.matcher(page); 
			if (m.find()) {
				transactionToken = m.group(1);
			}
			
			nvps.add(new BasicNameValuePair("isFormButtonClicked", "true"));
			nvps.add(new BasicNameValuePair("tabName", "Pending Transactions"));
			nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
		}
		
		log.info((new StringBuilder()).append("Clicking 'Pending Transactions' with ").append(nvps.toString())
				.toString());
		httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		page = EntityUtils.toString(entity);
		outputParams.put("page", page);
		
		p = Pattern.compile("<option value=\"(\\d+)\".*?>(.*?)</option>");
		m = p.matcher(page);
		AccountDropdownList addl = new AccountDropdownList();
		for (; m.find(); addl.addAccountDropdownItem(new AccountDropdownItem(m.group(1),
				m.group(2), "")))
			log.info((new StringBuilder()).append("Account name - ").append(m.group(2)).toString());

		outputParams.put("accounts", addl);
		
		p = Pattern
				.compile("<tr class=\"j?ext01\">\\s*<td>([^<]*)</td>\\s*<td class=\"aibTextStyle11\">([\\d,.]+)&nbsp;&nbsp;(DR)?</td></tr>");
		m = p.matcher(page);
		PendingTransactionList pendingTransactions = new PendingTransactionList();
		PendingTransaction t = null;
		while (m.find()) {
			t = new PendingTransaction(m.group(1), m.group(2), m.group(3), addl.getAccountById((String)inputParams.get("index")).getAccountName());
			pendingTransactions.addTransaction(t);
			log.info("Added: " + t.getNarrative());
		}
		outputParams.put("pendingtransactions", pendingTransactions.getTransactions());
		
		return outputParams;
	}
}