package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.syndicapp.scraper.aib.model.Account;

public class PageUtils {
	private static Logger log = Logger.getLogger(PageUtils.class);
	private static String[] months = new String[] {
		"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	};
	
	/**
	 * The referer URL is the first line of the page blob (must be manually added in each page implementation)
	 * 
	 * @param page
	 * @return The Referer URL
	 */
	static public String getReferer(String page) {
		return (page.split("\n"))[0];
	}
	
	static public int getMonthFromMonthName(String name) {
		for (int i=0; i<12; i++) {
			if (months[i].equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}
	
    static public ArrayList<Account> parseBalances(String page) {

    	Pattern p = Pattern.compile("<span>([\\s\\w\\d]+\\-\\d{3,4})</span>\\s*</li>\\s*<li class=\"balance\">\\s*<strong>Balance: </strong>\\s*<em>\\s*([\\d,.]+)\\s(DR)?\\s*</em>\\s*</li>\\s*<li>\\s*(&nbsp;|<strong>Available Funds: </strong>\\s*<em>([\\d,.]+)\\s*(DR)?</em>)\\s*</li>", Pattern.DOTALL);
        Matcher m = p.matcher(page);
        
        ArrayList<Account> accounts = new ArrayList<Account>();

        int i = 0; 
        while(m.find()) {
        	Account a = null;

        	if (m.groupCount() == 6) {
        		if (null == m.group(5)) {
        			a = new Account(i, m.group(1), m.group(2), m.group(3), false);
        			log.debug("Found account " + m.group(1));
        		} else {
        			a = new Account(i, m.group(1), m.group(5), m.group(6), true);
        			log.debug("Found account with available balance " + m.group(1));
        		}
        	}
            accounts.add(a);
        	i++;
        }

        return accounts;
    }
}
