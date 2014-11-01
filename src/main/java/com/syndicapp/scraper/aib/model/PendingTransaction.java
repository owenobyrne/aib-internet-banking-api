// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Transaction.java

package com.syndicapp.scraper.aib.model;

import org.apache.log4j.Logger;

public class PendingTransaction {
	static Logger Log = Logger.getLogger(PendingTransaction.class);
	String narrative;
	String amount;
	String account;
	boolean isDR;

	public PendingTransaction(String n, String amount, String dr, String account) {
		narrative = n;
		
		if ("".equals(dr)) {
			isDR = false;
		} else {
			isDR = true;
		}
		
		this.amount = amount;
		this.account = account;
		Log.debug(toString());
	}

	public String toString() {
		return (new StringBuilder()).append(narrative).append(": ").append(amount).append(" ")
				.append(isDR).toString();
	}

	public String getNarrative() {
		return narrative;
	}

	public String getAmount() {
		return amount;
	}

	public String getAccount() {
		return account;
	}

	public boolean getIsDR() {
		return isDR;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setDR(boolean isDR) {
		this.isDR = isDR;
	}

}