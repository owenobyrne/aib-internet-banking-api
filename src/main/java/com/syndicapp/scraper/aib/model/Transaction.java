package com.syndicapp.scraper.aib.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class Transaction {
    static Logger Log = Logger.getLogger(Transaction.class);
    GregorianCalendar transDate;
    String narrative;
    ArrayList<String> subNarrative;
    String amount;
    boolean isDR;
    String subsequentBalance;

    public Transaction(GregorianCalendar td, String n, String drcr, String a, String sb)
    {
        subNarrative = new ArrayList<String>();
        transDate = td;
        narrative = n;
        if(" credit".equals(drcr)) {
            isDR = false;
            amount = a;
            
        } else {
            isDR = true;
            amount = a;
        
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



}