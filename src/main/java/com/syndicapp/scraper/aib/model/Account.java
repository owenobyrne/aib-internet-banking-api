// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Account.java

package com.syndicapp.scraper.aib.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Account {

	static Logger Log = Logger.getLogger(Account.class);
	private int id;
	private String name;
	private BigDecimal balance;
	private String drcr;
	private ArrayList<Transaction> transactions;
	private boolean pending;

	public Account(int id, String name, String balance, String drcr, boolean pending) {
		Log.debug((new StringBuilder()).append("New Account - ").append(name).append(" - ")
				.append(balance).append(drcr).toString());
		this.id = id;
		this.name = name;
		this.balance = new BigDecimal(balance.replace(",", ""));
		this.drcr = (null == drcr ? "" : drcr);
		this.pending = pending;
	}

	public String getDrcr() {
		return drcr;
	}

	public void setDrcr(String drcr) {
		this.drcr = drcr;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBalance(String balance) {
		this.balance = new BigDecimal(balance.replace(",", ""));
	}

	public String getName() {
		return name;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public boolean isDR() {
		return drcr.equalsIgnoreCase("DR");
	}


	public boolean isPending() {
		return pending;
	}

	public void setTransactions(ArrayList<Transaction> t) {
		transactions = t;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}