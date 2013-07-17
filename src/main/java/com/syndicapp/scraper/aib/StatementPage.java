// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   StatementPage.java

package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.GregorianCalendar;
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
import com.syndicapp.scraper.aib.model.Transaction;
import com.syndicapp.scraper.aib.model.TransactionList;

public class StatementPage extends FSSUserAgent {

	public StatementPage() {
	}

	public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
			throws Exception {
		HashMap<String, Object> outputParams = new HashMap<String, Object>();
		HttpPost httppost = new HttpPost("https://aibinternetbanking.aib.ie/inet/roi/statement.htm");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		Pattern p = null;
		String transactionToken = null;
		Matcher m;
		if (inputParams.get("index") != null) {
			nvps.add(new BasicNameValuePair("index", (String) inputParams.get("index")));
			Log.debug("Clicked the drop down");
			p = Pattern
					.compile("action=\"statement.htm\" method=\"POST\" onsubmit=\"return isClickEnabled\\(\\)\">\\s*<div>\\s*<label>Change account:\\s*<select id=\"index\" name=\"index\">\\s*(.*?)\\s*</select>\\s*</label>\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
			m = p.matcher(page);
			if (m.find())
				transactionToken = m.group(2);
			nvps.add(new BasicNameValuePair("isFormButtonClicked", "true"));
			nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
		} else {
			Log.debug("Clicked the left menu");
			p = Pattern
					.compile("action=\"statement.htm\" method=\"post\"><input type=\"hidden\" name=\"isFormButtonClicked\" value=\"false\" /><input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
			for (m = p.matcher(page); m.find();)
				transactionToken = m.group(1);

			nvps.add(new BasicNameValuePair("isFormButtonClicked", "true"));
			nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
		}
		Log.info((new StringBuilder()).append("Clicking 'Statement' with ").append(nvps.toString())
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
			Log.info((new StringBuilder()).append("Account name - ").append(m.group(2)).toString());

		outputParams.put("accounts", addl);
		p = Pattern
				.compile("<tr class=\"j*ext01\">\\s*<td>(\\d\\d)/(\\d\\d)/(\\d\\d)</td>\\s*<td>([^<]*)</td>\\s*<td class=\"aibTextStyle10\">([^<]*)</td>\\s*<td class=\"aibTextStyle10\">([^<]*)</td>\\s*<td class=\"aibTextStyle10\">([^<]*)</td></tr>");
		m = p.matcher(page);
		TransactionList transactions = new TransactionList();
		Transaction t = null;
		while (m.find()) {
			if (m.group(4).toLowerCase().contains("interest rate")) {
				t = new Transaction(new GregorianCalendar(2000 + Integer.parseInt(m.group(3)),
						Integer.parseInt(m.group(2)) - 1, Integer.parseInt(m.group(1))),
						"New Interest Rate", m.group(5), m.group(6), m.group(7));
				transactions.addTransaction(t);
				System.out.println("Added new interest rate: " + t.getNarrative());
			} else if (t != null && "".equals(m.group(5)) && "".equals(m.group(6))) {
				t.addSubNarrative(m.group(4));
				transactions.replaceLastTransaction(t);
				System.out.println("Updated narrative: " + t.getNarrative());
			} else {
				t = new Transaction(new GregorianCalendar(2000 + Integer.parseInt(m.group(3)),
						Integer.parseInt(m.group(2)) - 1, Integer.parseInt(m.group(1))),
						m.group(4), m.group(5), m.group(6), m.group(7));
				transactions.addTransaction(t);
				System.out.println("Added: " + t.getNarrative());
			}
			System.out.println("Found transaction: " + t.getNarrative());
		}
		outputParams.put("transactions", transactions);
		return outputParams;
	}
}