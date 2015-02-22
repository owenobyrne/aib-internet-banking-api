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
import com.syndicapp.scraper.aib.model.PendingTransaction;
import com.syndicapp.scraper.aib.model.PendingTransactionList;
import com.syndicapp.scraper.aib.model.Transaction;
import com.syndicapp.scraper.aib.model.TransactionList;
import com.syndicapp.scraper.exception.UnexpectedPageContentsException;

public class PendingTransactionsPage extends FSSUserAgent {

	public PendingTransactionsPage() {
	}

	public static HashMap<String, Object> click(String page, HashMap<String, Object> inputParams)
			throws Exception {
		String thisPage = "https://onlinebanking.aib.ie/inet/roi/pendingtransactions.htm";
		
		HashMap<String, Object> outputParams = new HashMap<String, Object>();
		HttpPost httppost = new HttpPost(thisPage);
		log.trace(page);
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		Pattern p = null;
		Matcher m;
		
		// I'm on the Recent Transactions page and I'm clicking the Pending Transactions tab.
		log.debug("Clicked the Pending Transactions Button");
		p = Pattern.compile("action=\"pendingtransactions.htm\" method=\"POST\" onsubmit=\"return isClickEnabled\\(\\)\">\\s*<input type=\"hidden\" name=\"index\" value=\"(\\d+)\" />\\s*<input type=\"hidden\" name=\"stmtIndex\" value=\"(\\d+)\" />\\s*<button name=\"tabName\" value=\"Pending Transactions\">Pending</button>\\s*<input type=\"hidden\" name=\"transactionToken\" id=\"transactionToken\" value=\"(\\d+)\"/>");
		m = p.matcher(page); 

		String index = "";
		
		if (m.find()) {
			index = m.group(1);
			
			nvps.add(new BasicNameValuePair("index", m.group(1)));
			nvps.add(new BasicNameValuePair("stmtIndex", m.group(2)));
			nvps.add(new BasicNameValuePair("transactionToken", m.group(3)));
			nvps.add(new BasicNameValuePair("tabName", "Pending Transactions"));
			nvps.add(new BasicNameValuePair("iBankFormSubmission", "false"));
			
		} else {
			throw new UnexpectedPageContentsException("Can't find the Pending Transactions Button!");
		}
		
		log.info((new StringBuilder()).append("Clicking 'Pending Transactions' with ").append(nvps.toString())
				.toString());
		httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		httppost.setHeader("Referer", PageUtils.getReferer(page));
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		page = EntityUtils.toString(entity);
		outputParams.put("page", thisPage + "\n" + page);
		
		p = Pattern.compile("<option value=\"(\\d+)\".*?>(.*?)</option>");
		m = p.matcher(page);
		AccountDropdownList addl = new AccountDropdownList();
		for (; m.find(); addl.addAccountDropdownItem(new AccountDropdownItem(m.group(1),
				m.group(2), "")))
			log.info((new StringBuilder()).append("Account name - ").append(m.group(2)).toString());

		outputParams.put("accounts", addl);
		
		
		Pattern pDate = Pattern.compile("<li><strong>.*?, (\\d{1,2})\\S\\S (\\S*) (\\d\\d)</strong></li>\\s*<li></li>");
		Pattern pTrans = Pattern.compile("<li class=\"forceWrap\">([^<]*)</li>\\s*<li class=\"alignr debit\">([^<]*)<span></span></li>");
		Matcher m1;
		
		PendingTransactionList pendingTransactions = new PendingTransactionList();
		PendingTransaction t = null;
		GregorianCalendar date = null;
		
		p = Pattern.compile("<ul(.*?)</ul>", Pattern.DOTALL);
		m = p.matcher(page);
		while (m.find()) {
			
			log.debug(m.group(1));
			String row = m.group(1);
			
			m1 = pDate.matcher(row);
			if (m1.find()) {
				log.debug(m1.group(1) + "-" + m1.group(2) + "-" + m1.group(3));
				date = new GregorianCalendar(
					2000 + Integer.parseInt(m1.group(3)), 
					PageUtils.getMonthFromMonthName(m1.group(2)),
					Integer.parseInt(m1.group(1))
				);
				continue;
			}
			
			m1 = pTrans.matcher(row);
			if (m1.find()) {
				log.debug(m1.group(1) + "-" + m1.group(2));
				
				t = new PendingTransaction(
					m1.group(1), 
					m1.group(2), 
					addl.getAccountById(index).getAccountName()
				);
				pendingTransactions.addTransaction(t);
				log.info("Added: " + t.getNarrative());
				
				continue;
			}
			
		}
			
		outputParams.put("pendingtransactions", pendingTransactions.getTransactions());
		
		return outputParams;
	}
}