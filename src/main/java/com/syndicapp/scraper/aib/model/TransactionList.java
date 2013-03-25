// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TransactionList.java

package com.syndicapp.scraper.aib.model;

import java.util.Vector;

// Referenced classes of package com.syndicapp.scraper.aib.model:
//            Transaction

public class TransactionList
{

    public TransactionList()
    {
        transactions = new Vector<Transaction>();
    }

    public Vector<Transaction> getTransactions()
    {
        return transactions;
    }

    public void addTransaction(Transaction t)
    {
        transactions.add(t);
    }

    public void replaceLastTransaction(Transaction t)
    {
        transactions.setElementAt(t, transactions.size() - 1);
    }

    public void setTransactions(Vector<Transaction> transactions)
    {
        this.transactions = transactions;
    }

    private Vector<Transaction> transactions;
}