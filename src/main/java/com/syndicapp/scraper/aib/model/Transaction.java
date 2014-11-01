// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Transaction.java

package com.syndicapp.scraper.aib.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Transaction
{

    public Transaction(GregorianCalendar td, String n, String dr, String cr, String sb)
    {
        subNarrative = new ArrayList<String>();
        transDate = td;
        narrative = n;
        if("".equals(cr))
        {
            isDR = true;
            amount = dr;
        } else
        {
            isDR = false;
            amount = cr;
        }
        subsequentBalance = sb.replace("&nbsp;", "");
        Log.debug(toString());
    }

    public String toString()
    {
        return (new StringBuilder()).append(narrative).append(": ").append(amount).append(" ").append(transDate.toString()).toString();
    }

    public GregorianCalendar getTransDate()
    {
        return transDate;
    }

    public void addSubNarrative(String sn)
    {
        subNarrative.add(sn);
    }

    public String getNarrative()
    {
        return narrative;
    }

    public String getAmount()
    {
        return amount;
    }

    public boolean getIsDR()
    {
        return isDR;
    }

    public String getSubNarrative()
    {
        if(subNarrative.isEmpty())
            return "";
        Iterator<String> iter = subNarrative.iterator();
        StringBuffer buffer = new StringBuffer((String)iter.next());
        for(; iter.hasNext(); buffer.append(" / ").append((String)iter.next()));
        return buffer.toString();
    }

    public String getSubsequentBalance()
    {
        return subsequentBalance;
    }

    public void setSubsequentBalance(String subsequentBalance)
    {
        this.subsequentBalance = subsequentBalance;
    }

    public void setTransDate(GregorianCalendar transDate)
    {
        this.transDate = transDate;
    }

    public void setNarrative(String narrative)
    {
        this.narrative = narrative;
    }

    public void setSubNarrative(ArrayList<String> subNarrative)
    {
        this.subNarrative = subNarrative;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public void setDR(boolean isDR)
    {
        this.isDR = isDR;
    }

    static Logger Log = Logger.getLogger(Transaction.class);
    GregorianCalendar transDate;
    String narrative;
    ArrayList<String> subNarrative;
    String amount;
    boolean isDR;
    String subsequentBalance;

}