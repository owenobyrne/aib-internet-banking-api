// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TransferBetweenMyOwnAccountsConfirmationPage.java

package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.syndicapp.scraper.FSSUserAgent;
import com.syndicapp.scraper.exception.UnexpectedPageContentsException;

public class TransferBetweenMyOwnAccountsConfirmationPage extends FSSUserAgent {

	public TransferBetweenMyOwnAccountsConfirmationPage() {
	}

	public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
			throws Exception {
		HashMap<String, Object> outputParams = new HashMap<String, Object>();
		String transactionToken = null;
		Pattern p = Pattern
				.compile("transactionToken\" value=\"(\\d+)\"/>\\s*<input type=\"hidden\" name=\"iBankFormSubmission\" value=\"true\" />\\s*<input type=\"hidden\" name=\"_finish");
		for (Matcher m = p.matcher(page); m.find();)
			transactionToken = m.group(1);

		HttpPost httppost = new HttpPost(
				"https://aibinternetbanking.aib.ie/inet/roi/fundstransferownaccounts.htm");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("confirmPac.pacDigit", (String) inputParams.get("digit")));
		nvps.add(new BasicNameValuePair("_finish", "true"));
		nvps.add(new BasicNameValuePair("_finish.x", "49"));
		nvps.add(new BasicNameValuePair("_finish.y", "8"));
		nvps.add(new BasicNameValuePair("iBankFormSubmission", "true"));
		nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
		log.fatal((new StringBuilder())
				.append("Clicking 'Transfers between my own accounts confirmation' with ")
				.append(nvps.toString()).toString());
		httppost.setEntity(new UrlEncodedFormEntity(nvps, "ISO-8859-1"));
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		page = EntityUtils.toString(entity);
		if (!page.contains("Your funds have been transferred.")) {
			throw new UnexpectedPageContentsException(
					"Didn't get to the Transfers between my own accounts confirmation Page!");
		} else {
			outputParams.put("page", page);
			return outputParams;
		}
	}
}