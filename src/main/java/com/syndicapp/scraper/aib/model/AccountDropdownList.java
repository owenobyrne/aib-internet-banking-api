// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AccountDropdownList.java

package com.syndicapp.scraper.aib.model;

import java.util.Iterator;
import java.util.Vector;

// Referenced classes of package com.syndicapp.scraper.aib.model:
//            AccountDropdownItem

public class AccountDropdownList {
	private Vector<AccountDropdownItem> accountDropdownItems;

	public AccountDropdownList() {
		accountDropdownItems = new Vector<AccountDropdownItem>();
	}

	public void addAccountDropdownItem(AccountDropdownItem a) {
		accountDropdownItems.add(a);
	}

	public AccountDropdownItem getAccountByName(String name) {
		for (Iterator<AccountDropdownItem> itr = accountDropdownItems.iterator(); itr.hasNext();) {
			AccountDropdownItem a = (AccountDropdownItem) itr.next();
			if (a.getAccountName().equalsIgnoreCase(name))
				return a;
		}

		return null;
	}

	public AccountDropdownItem getAccountById(String index) {
		if (index == null) {
			return accountDropdownItems.get(0);
		} else {
			int i = Integer.parseInt(index);
			return accountDropdownItems.get(i);
		}
	}

	public Vector<AccountDropdownItem> getAccountDropdownItems() {
		return accountDropdownItems;
	}

	public void setAccountDropdownItems(Vector<AccountDropdownItem> accountDropdownItems) {
		this.accountDropdownItems = accountDropdownItems;
	}

}