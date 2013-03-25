// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Payee.java

package com.syndicapp.scraper.aib.model;


public class Payee
{

    public Payee(String name, String accountNumber, String sortCode)
    {
        this.name = name;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    public String getName()
    {
        return name;
    }

    public String getBankDetails()
    {
        return (new StringBuilder()).append(sortCode).append(accountNumber).toString();
    }

    private String name;
    private String accountNumber;
    private String sortCode;
}