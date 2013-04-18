package com.owenobyrne.aibibs.services;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.syndicapp.scraper.aib.AccountOverviewPage;
import com.syndicapp.scraper.aib.LoginPage;
import com.syndicapp.scraper.aib.LogoutPage;
import com.syndicapp.scraper.aib.PACAndChallengePage;
import com.syndicapp.scraper.aib.RegistrationNumberPage;
import com.syndicapp.scraper.aib.StatementPage;
import com.syndicapp.scraper.aib.model.TransactionList;
import com.syndicapp.scraper.exception.UnexpectedPageContentsException;

@Component
public class AibInternetBankingService
{

    public HashMap<String, Object> enterRegistrationNumber(String registrationNumber)
    {
        HashMap<String, Object> input = new HashMap<String, Object>();
        HashMap<String, Object> stringOutput = new HashMap<String, Object>();
        try
        {
            stringOutput = LoginPage.click();
            input.put("regNumber", registrationNumber);
            stringOutput = RegistrationNumberPage.click((String)stringOutput.get("page"), input);
            log.info((new StringBuilder()).append("Looking for digits ").append((String)stringOutput.get("digit1")).append(", ").append((String)stringOutput.get("digit2")).append(" and ").append((String)stringOutput.get("digit3")).append(" of PAC, and the ").append((String)stringOutput.get("howmuch")).append("of your ").append((String)stringOutput.get("whatvalue")).toString());
        }
        catch(UnexpectedPageContentsException upce)
        {
            log.fatal((new StringBuilder()).append("Error with click(): ").append(upce.getMessage()).toString());
        }
        catch(Exception e)
        {
            log.fatal(e);
        }
        return stringOutput;
    }

    public HashMap<String, Object> enterPACDigits(String page, String pac1, String pac2, String pac3, String digits)
    {
        HashMap<String, Object> input = new HashMap<String, Object>();
        HashMap<String, Object> output = new HashMap<String, Object>();
        try
        {
            input = new HashMap<String, Object>();
            input.put("pacDetails.pacDigit1", pac1);
            input.put("pacDetails.pacDigit2", pac2);
            input.put("pacDetails.pacDigit3", pac3);
            input.put("challengeDetails.challengeEntered", digits);
            output = PACAndChallengePage.click(page, input);
        }
        catch(UnexpectedPageContentsException upce)
        {
            log.fatal((new StringBuilder()).append("Error with click(): ").append(upce.getMessage()).toString());
        }
        catch(Exception e)
        {
            log.fatal(e);
        }
        return output;
    }

    public HashMap<String, Object> logout(String page)
    {
        HashMap<String, Object> objectOutput = new HashMap<String, Object>();
        try
        {
            LogoutPage.click(page);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return objectOutput;
    }

    public HashMap<String, Object> getTransactionsForAccount(String page, String accountId)
    {
        HashMap<String, Object> input = new HashMap<String, Object>();
        HashMap<String, Object> objectOutput = new HashMap<String, Object>();
        try
        {
            objectOutput = StatementPage.click(page, input);
            input.put("index", accountId);
            objectOutput = StatementPage.click((String)objectOutput.get("page"), input);
            TransactionList tl = (TransactionList)objectOutput.get("transactions");
            objectOutput = AccountOverviewPage.click((String)objectOutput.get("page"), null);
            objectOutput.put("transactions", tl);
            return objectOutput;
        }
        catch(UnexpectedPageContentsException upce)
        {
            log.fatal((new StringBuilder()).append("Error with click(): ").append(upce.getMessage()).toString());
        }
        catch(Exception e)
        {
            log.fatal(e);
        }
        return objectOutput;
    }

    public void setupPayerAndTransfer(Long long1, String s, String s1, String s2)
    {
    }

    private static Logger log = Logger.getLogger(AibInternetBankingService.class);

}