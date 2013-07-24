package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.syndicapp.scraper.aib.model.Account;

public class PageUtils {
	private static Logger log = Logger.getLogger(PageUtils.class);

    static public ArrayList<Account> parseBalances(String page) {
    	// regex yeeeehaw!!! What a bitch this was to figure out!
        Pattern p = Pattern.compile("<span>([\\s\\w\\d]+\\-\\d{3,4})</span>\\s*</button>\\s*</form>(?:(?!quickPayCommand).)*?<h3>\\s*([\\d,.]+)\\s(DR)?\\s*[&<]((?:(?!quickPayCommand).)*?Available Funds.*?([\\d,.]+)\\s?(DR)?&)?", Pattern.DOTALL);
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
