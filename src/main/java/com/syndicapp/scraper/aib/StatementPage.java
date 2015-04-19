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
		String thisPage = "https://onlinebanking.aib.ie/inet/roi/statement.htm";
		
		HashMap<String, Object> outputParams = new HashMap<String, Object>();
		HttpPost httppost = new HttpPost(thisPage);
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		Pattern p = null;
		String transactionToken = null;
		Matcher m;

		log.debug(page);
		
		if (inputParams.get("index") != null) {
			nvps.add(new BasicNameValuePair("index", (String) inputParams.get("index")));
			log.debug("Clicked the drop down");
			
			p = Pattern.compile("action=\"statement.htm\" method=\"POST\" onsubmit=\"return isClickEnabled\\(\\)\">\\s*<select id=\"index\" name=\"index\" onchange=\".*?\">\\s*(.*?)\\s*</select>\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
			
			m = p.matcher(page);
			if (m.find()) {
				transactionToken = m.group(2);
			}
			nvps.add(new BasicNameValuePair("isFormButtonClicked", "true"));
			nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
			
		} else {
			log.debug("Clicked the top menu");
			p = Pattern.compile("id=\"statement_form_id\" action=\"statement.htm\" method=\"post\" onsubmit=\"return isFormClickEnabled\\(this\\)\">\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
			m = p.matcher(page); 
			if (m.find()) {
				transactionToken = m.group(1);
			}

			nvps.add(new BasicNameValuePair("transactionToken", transactionToken));
			nvps.add(new BasicNameValuePair("isFormButtonClicked", "true"));
			nvps.add(new BasicNameValuePair("index", "0"));
			
		}
		
		log.info((new StringBuilder()).append("Clicking 'Statement' with ").append(nvps.toString()).toString());
		
		httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		httppost.setHeader("Referer", PageUtils.getReferer(page));
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		
		page = EntityUtils.toString(entity);
		outputParams.put("page", thisPage + "\n" + page);
		log.debug(page);
		
		p = Pattern.compile("<option value=\"(\\d+)\".*?>(.*?)</option>");
		m = p.matcher(page);
		AccountDropdownList addl = new AccountDropdownList();
		while (m.find()) {
			addl.addAccountDropdownItem(new AccountDropdownItem(m.group(1), m.group(2), ""));
			log.info((new StringBuilder()).append("Account name - ").append(m.group(2)).toString());
		}
		outputParams.put("accounts", addl);
		
		Pattern pDate = Pattern.compile("<td colspan=\"3\" class=\"hide-td-large\"><strong>.*?, (\\d{1,2})\\S\\S (\\S*) (\\d\\d)</strong></td>");
		//Pattern pTrans = Pattern.compile("<td class=\"forceWrap\">([^<]*)</td>\\s*<td class=\"alignr(.*?)\">([^<]*)<span></span></td>\\s*<td class=\"alignr\">([^<]*)<span>");
		Pattern pTrans = Pattern.compile("<td class=\"forceWrap\">([^<]*)</td>\\s*<td class=\"alignr(.*?)\">(?:<i class=\"hide-large\">)?(<span>.</span>([^<]*))?(?:</i>)?</td>");
		Matcher m1;
		
		TransactionList transactions = new TransactionList();
		Transaction t = null;
		GregorianCalendar date = null;
		
		p = Pattern.compile("<tr(.*?)</tr>", Pattern.DOTALL);
		m = p.matcher(page);
		while (m.find()) {
			
			//log.info(m.group(1));
			String row = m.group(1);
			
			m1 = pDate.matcher(row);
			if (m1.find()) {
				//log.info(m1.group(1) + "-" + m1.group(2) + "-" + m1.group(3));
				date = new GregorianCalendar(
					2000 + Integer.parseInt(m1.group(3)), 
					PageUtils.getMonthFromMonthName(m1.group(2)),
					Integer.parseInt(m1.group(1))
				);
				continue;
			}
			
			m1 = pTrans.matcher(row);
			if (m1.find()) {
				//log.info(m1.groupCount() + " " + m1.group(1));
				
				if (m1.group(1).toLowerCase().contains("interest rate")) {
					t = new Transaction(
							date,
							"New Interest Rate", 
							"", 
							"0.00"
					);
					transactions.addTransaction(t);
					log.info("New interest Rate");
			
				} else if (t != null && "".equals(m1.group(2))) {
					t.addSubNarrative(m1.group(1));
					transactions.replaceLastTransaction(t);
					log.info("Updated narrative: " + t.getNarrative());
					
				} else if (m1.groupCount() == 4) {
					//log.info(m1.group(1) + "/" + m1.group(2) + "/" + m1.group(3) + "/" + m1.group(4));
				
					t = new Transaction(
						date,
						m1.group(1), 
						m1.group(2), 
						m1.group(4)
					);
					transactions.addTransaction(t);
					//log.info("Added: " + t.getNarrative());
					
				}
				log.info("Found transaction: " + t.getNarrative());
				continue;
			}
			
		}
		
		outputParams.put("transactions", transactions);
		return outputParams;
	}
	

}