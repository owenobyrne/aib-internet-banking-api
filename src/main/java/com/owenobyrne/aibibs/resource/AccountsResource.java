package com.owenobyrne.aibibs.resource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.cassandra.thrift.Cassandra.login_args;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.owenobyrne.aibibs.services.AibInternetBankingService;
import com.owenobyrne.aibibs.services.CassandraService;
import com.syndicapp.scraper.aib.model.PendingTransaction;

@Path("/accounts")
@Component
public class AccountsResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	@Context 
	HttpServletResponse response;


	@Autowired
	AibInternetBankingService aibibs;
	@Autowired
	CassandraService cassandra;

	@Path("/balances")
	@GET
	@Produces(value = "application/json")
	public HashMap<String, Object> getAccountBalances(
			@QueryParam("sessionId") String sessionId
			) {

		//String sessionId = (String) pacParams.getFirst("SESSION_ID");
		String page = cassandra.getData(CassandraService.CF_SESSIONS, sessionId, "page");

		if (page != null) {
			HashMap<String, Object> response = aibibs.getAccountBalances(page);
			cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page",
					(String) response.get("page"), 390);
			//response.put("sessionId", sessionId);
			response.remove("page");
			return response;
		} else {
			cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
			HashMap<String, Object> r = new HashMap<String, Object>();
			r.put("error", "Session has expired");
			return r;
		}
	}

	@Path("/{accountName}/transactions")
	@GET
	@Produces(value = "application/json")
	public HashMap<String, Object> getTransactions(
			@QueryParam("sessionId") String sessionId,
			@PathParam(value = "accountName") String accountName
		) {

		//String sessionId = params.getFirst("SESSION_ID");
		String page = cassandra.getData(CassandraService.CF_SESSIONS, sessionId, "page");
		if (page != null) {
			HashMap<String, Object> response = aibibs.getTransactionsForAccount(page, accountName);
			cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page",
					(String) response.get("page"), 390);
			//response.put("sessionId", sessionId);
			response.remove("page");
			response.remove("accounts");
			response.remove("balances");
			return response;
		} else {
			cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
			HashMap<String, Object> r = new HashMap<String, Object>();
			r.put("error", "Session has expired");
			return r;
		}
	}

	@Path("/{accountNameFrom}/transferTo/{accountNameTo}")
	@POST
	@Consumes(value="application/x-www-form-urlencoded")
	@Produces(value = "application/json")
	public HashMap<String, Object> transfer(
			@QueryParam("sessionId") String sessionId,
			@PathParam(value = "accountNameFrom") String accountNameFrom,
			@PathParam(value = "accountNameTo") String accountNameTo,
			MultivaluedMap<String, String> transferParams
		) {

		String page = cassandra.getData(CassandraService.CF_SESSIONS, sessionId, "page");
		if (page != null) {
			
			String narrativeFrom = transferParams.getFirst("narrativeFrom");
			String narrativeTo = transferParams.getFirst("narrativeTo");
			String pinDigits = transferParams.getFirst("pinDigits");
			BigDecimal amount = new BigDecimal(transferParams.getFirst("amount"));
			
			HashMap<String, Object> response = aibibs.transferBetweenAccounts(page, 
					accountNameFrom, 
					accountNameTo, 
					narrativeFrom,
					narrativeTo,
					amount,
					pinDigits);

			
			cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page",
					(String) response.get("page"), 390);
			
			response.remove("page");
			response.remove("accounts");
			response.remove("balances");
			return response;
		} else {
			cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
			HashMap<String, Object> r = new HashMap<String, Object>();
			r.put("error", "Session has expired");
			return r;
		}
	}

	@Path("/{accountName}/pending")
	@GET
	@Produces(value = "application/json")
	public Vector<PendingTransaction> getPendingTransactions(
			@QueryParam("sessionId") String sessionId,
			@PathParam(value = "accountName") String accountName
		) {

		String page = cassandra.getData(CassandraService.CF_SESSIONS, sessionId, "page");
		if (page != null) {
			HashMap<String, Object> response = aibibs.getPendingTransactionsForAccount(page, accountName);
			cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page",
					(String) response.get("page"), 390);
			
			return (Vector<PendingTransaction>)response.get("pendingtransactions");
		} else {
			cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
			response.setHeader("X-AIBAPI-Error", "Session has expired");
			return null;
		}
	}

}