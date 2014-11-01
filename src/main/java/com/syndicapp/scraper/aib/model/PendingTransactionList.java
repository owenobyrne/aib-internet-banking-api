// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TransactionList.java

package com.syndicapp.scraper.aib.model;

import java.util.Vector;

import org.apache.log4j.Logger;

// Referenced classes of package com.syndicapp.scraper.aib.model:
//            Transaction

public class PendingTransactionList {
	static Logger log = Logger.getLogger(PendingTransactionList.class);
	
	private Vector<PendingTransaction> transactions;

	public PendingTransactionList() {
		transactions = new Vector<PendingTransaction>();
	}

	public Vector<PendingTransaction> getTransactions() {
		log.trace(transactions);
		return transactions;
	}

	public void addTransaction(PendingTransaction t) {
		transactions.add(t);
	}

}