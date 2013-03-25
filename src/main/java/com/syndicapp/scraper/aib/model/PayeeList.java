// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PayeeList.java

package com.syndicapp.scraper.aib.model;

import java.util.Iterator;
import java.util.Vector;

// Referenced classes of package com.syndicapp.scraper.aib.model:
//            Payee

public class PayeeList
{

    public PayeeList()
    {
        payees = new Vector<Payee>();
    }

    public void addPayee(Payee a)
    {
        payees.add(a);
    }

    public Payee getPayeeByName(String name)
    {
        for(Iterator<Payee> itr = payees.iterator(); itr.hasNext();)
        {
            Payee p = (Payee)itr.next();
            if(p.getName().equalsIgnoreCase(name))
                return p;
        }

        return null;
    }

    private Vector<Payee> payees;
}