// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AccountDropdownItem.java

package com.syndicapp.scraper.aib.model;


public class AccountDropdownItem
{

    public AccountDropdownItem(String accountId, String accountName, String accountBalance)
    {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountBalance = accountBalance;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public String getAccountBalance()
    {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance)
    {
        this.accountBalance = accountBalance;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    private String accountId;
    private String accountName;
    private String accountBalance;
}