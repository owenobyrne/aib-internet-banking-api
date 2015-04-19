package com.owenobyrne.aibibs.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.syndicapp.scraper.aib.AccountOverviewPage;
import com.syndicapp.scraper.aib.LoginPage;
import com.syndicapp.scraper.aib.LogoutPage;
import com.syndicapp.scraper.aib.PACAndChallengePage;
import com.syndicapp.scraper.aib.PendingTransactionsPage;
import com.syndicapp.scraper.aib.PostLoginInformationPage;
import com.syndicapp.scraper.aib.RegistrationNumberPage;
import com.syndicapp.scraper.aib.StatementPage;
import com.syndicapp.scraper.aib.TransferBetweenMyOwnAccountsConfirmationPage;
import com.syndicapp.scraper.aib.TransferBetweenMyOwnAccountsPage;
import com.syndicapp.scraper.aib.TransferBetweenMyOwnAccountsStep1Page;
import com.syndicapp.scraper.aib.TransfersAndPaymentsPage;
import com.syndicapp.scraper.aib.model.AccountDropdownItem;
import com.syndicapp.scraper.aib.model.AccountDropdownList;
import com.syndicapp.scraper.aib.model.PendingTransaction;
import com.syndicapp.scraper.aib.model.TransactionList;
import com.syndicapp.scraper.exception.UnexpectedPageContentsException;

@Component
public class AibInternetBankingService {
	private static Logger log = Logger.getLogger(AibInternetBankingService.class);

	public HashMap<String, Object> enterRegistrationNumber(String registrationNumber) {
		HashMap<String, Object> input = new HashMap<String, Object>();
		HashMap<String, Object> stringOutput = new HashMap<String, Object>();
		try {
			stringOutput = LoginPage.click();
			input.put("regNumber", registrationNumber);
			stringOutput = RegistrationNumberPage.click((String) stringOutput.get("page"), input);
			log.info((new StringBuilder()).append("Looking for digits ")
					.append((String) stringOutput.get("digit1")).append(", ")
					.append((String) stringOutput.get("digit2")).append(" and ")
					.append((String) stringOutput.get("digit3")).append(" of PAC"));
		} catch (UnexpectedPageContentsException upce) {
			log.fatal((new StringBuilder()).append("Error with click(): ")
					.append(upce.getMessage()).toString());
		} catch (Exception e) {
			log.fatal(e);
		}
		return stringOutput;
	}

	public HashMap<String, Object> enterPACDigits(String page, String pac1, String pac2,
			String pac3, String digits) {
		HashMap<String, Object> input = new HashMap<String, Object>();
		HashMap<String, Object> output = new HashMap<String, Object>();
		try {
			input = new HashMap<String, Object>();
			input.put("pacDetails.pacDigit1", pac1);
			input.put("pacDetails.pacDigit2", pac2);
			input.put("pacDetails.pacDigit3", pac3);
			output = PACAndChallengePage.click(page, input);
			
			if ((Boolean)output.get("infoPage")) {
				// there's a post login info page!
				log.info("There's a Post-Login Information page - attempting to click through...");
				output = PostLoginInformationPage.click((String)output.get("page"), null);	
			} 
			
		} catch (UnexpectedPageContentsException upce) {
			log.fatal((new StringBuilder()).append("Error with click(): ")
					.append(upce.getMessage()).toString());
		} catch (Exception e) {
			log.fatal(e);
		}
		return output;
	}

	public HashMap<String, Object> getAccountBalances(String page) {
		HashMap<String, Object> input = new HashMap<String, Object>();
		HashMap<String, Object> output = new HashMap<String, Object>();
		try {
			input = new HashMap<String, Object>();
			output = AccountOverviewPage.click(page, input);
		} catch (UnexpectedPageContentsException upce) {
			log.fatal((new StringBuilder()).append("Error with click(): ")
					.append(upce.getMessage()).toString());
		} catch (Exception e) {
			log.fatal(e);
		}
		return output;
	}

	public HashMap<String, Object> logout(String page) {
		HashMap<String, Object> objectOutput = new HashMap<String, Object>();
		try {
			LogoutPage.click(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectOutput;
	}

	public HashMap<String, Object> getTransactionsForAccount(String page, String accountName) {
		HashMap<String, Object> input = new HashMap<String, Object>();
		HashMap<String, Object> objectOutput = new HashMap<String, Object>();
		try {
			objectOutput = StatementPage.click(page, input);
			AccountDropdownList adl = (AccountDropdownList) objectOutput.get("accounts");
			AccountDropdownItem a = adl.getAccountByName(accountName);

			log.info("Wanted: " + accountName + ", got " + a.getAccountName());

			// Check if we are looking for the first account or not
			if (!a.getAccountId().equals("0")) {
				// Select the right account from the dropdown list.
				input.put("index", a.getAccountId());
				objectOutput = StatementPage.click((String) objectOutput.get("page"), input);
			}

			TransactionList tl = (TransactionList) objectOutput.get("transactions");

			// Return to the home page.
			objectOutput = AccountOverviewPage.click((String) objectOutput.get("page"), null);
			objectOutput.put("transactions", tl);
			return objectOutput;
		} catch (UnexpectedPageContentsException upce) {
			log.fatal((new StringBuilder()).append("Error with click(): ")
					.append(upce.getMessage()).toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.fatal(e);
		}
		return objectOutput;
	}

	public HashMap<String, Object> getPendingTransactionsForAccount(String page, String accountName) {
		HashMap<String, Object> input = new HashMap<String, Object>();
		HashMap<String, Object> objectOutput = new HashMap<String, Object>();
		try {
			objectOutput = StatementPage.click(page, input);
			AccountDropdownList adl = (AccountDropdownList) objectOutput.get("accounts");
			AccountDropdownItem a = adl.getAccountByName(accountName);

			log.info("Wanted: " + accountName + ", got " + a.getAccountName());

			// Check if we are looking for the first account or not
			if (!a.getAccountId().equals("0")) {
				// Select the right account from the dropdown list.
				input.put("index", a.getAccountId());
				objectOutput = StatementPage.click((String) objectOutput.get("page"), input);
			}
			
			input.remove("index");
			objectOutput = PendingTransactionsPage.click((String) objectOutput.get("page"), input);
			
			
			@SuppressWarnings("unchecked")
			Vector<PendingTransaction> t = (Vector<PendingTransaction>) objectOutput.get("pendingtransactions");

			// Return to the home page.
			objectOutput = AccountOverviewPage.click((String) objectOutput.get("page"), null);

			objectOutput.put("pendingtransactions", t);

			return objectOutput;
		} catch (UnexpectedPageContentsException upce) {
			log.fatal((new StringBuilder()).append("Error with click(): ")
					.append(upce.getMessage()).toString());
		} catch (Exception e) {
			log.fatal(e);
		}
		return objectOutput;
	}

	public HashMap<String, Object> transferBetweenAccounts(String page, 
			String accountNameFrom, 
			String accountNameTo, 
			String narrativeFrom,
			String narrativeTo,
			BigDecimal amount,
			String pinDigits) {
		HashMap<String, Object> input = new HashMap<String, Object>();
		HashMap<String, Object> output = new HashMap<String, Object>();

		try {			
			output = TransfersAndPaymentsPage.click(page, input);
			output = TransferBetweenMyOwnAccountsPage.click((String) output.get("page"), input);

			AccountDropdownItem aFrom = ((AccountDropdownList) output.get("accountsFrom"))
					.getAccountByName(accountNameFrom);
			AccountDropdownItem aTo = ((AccountDropdownList) output.get("accountsTo"))
					.getAccountByName(accountNameTo);

			BigDecimal[] amountBits = amount.divideAndRemainder(new BigDecimal("1.0"));

			input.put("fromAccount", aFrom.getAccountId());
			input.put("senderReference", (narrativeFrom.length() > 12 ? narrativeFrom.substring(0,  12) : narrativeFrom));
			input.put("toAccount", aTo.getAccountId());
			input.put("receiverReference", (narrativeTo.length() > 18 ? narrativeTo.substring(0,  18) : narrativeTo));
			input.put("amounteuro", String.format("%d", amountBits[0].intValue()));
			input.put("amountcent", String.format("%02d", amountBits[1].multiply(new BigDecimal("100.0")).intValue()));

			output = TransferBetweenMyOwnAccountsStep1Page.click((String)output.get("page"), input);
			
			String[] digits = pinDigits.split("");
			input.put("digit", digits[Integer.parseInt((String)output.get("digitRequested"))]);
			output = TransferBetweenMyOwnAccountsConfirmationPage.click((String)output.get("page"), input);

			output = AccountOverviewPage.click((String)output.get("page"), input);

		} catch (Exception e) {
			log.fatal(e);
			e.printStackTrace();
		}
		return output;

	}

	public void setupPayerAndTransfer(Long long1, String s, String s1, String s2) {
	}

}