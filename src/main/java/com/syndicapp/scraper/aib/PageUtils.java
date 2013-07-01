package com.syndicapp.scraper.aib;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.syndicapp.scraper.aib.model.Account;

public class PageUtils {

    static public ArrayList<Account> parseBalances(String page) {
    	// regex yeeeehaw!!! What a bitch this was to figure out!
        Pattern p = Pattern.compile("<span>([\\s\\w\\d]+\\-\\d{3,4})</span>\\s*</button>\\s*</form>(?:(?!transactionToken).)*?<h3>\\s*([\\d,.]+)\\s(DR)?\\s*[&<]((?:(?!transactionToken).)*?Available Funds.*?([\\d,.]+)\\s?(DR)?&)?", Pattern.DOTALL);
        Matcher m = p.matcher(page);
        
        ArrayList<Account> accounts = new ArrayList<Account>();

        int i = 1; 
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

        return accounts;
    }
}
