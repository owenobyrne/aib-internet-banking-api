package com.owenobyrne.aibibs.resource;

import java.util.HashMap;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.owenobyrne.aibibs.services.AibInternetBankingService;
import com.owenobyrne.aibibs.services.CassandraService;

@Path("/accounts")
@Component
public class AccountsResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

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

	@Path("/{accountId}/transactions")
	@GET
	@Produces(value = "application/json")
	public HashMap<String, Object> getTransactions(
			@QueryParam("sessionId") String sessionId,
			@PathParam(value = "accountId") String accountId
		) {

		//String sessionId = params.getFirst("SESSION_ID");
		String page = cassandra.getData(CassandraService.CF_SESSIONS, sessionId, "page");
		if (page != null) {
			HashMap<String, Object> response = aibibs.getTransactionsForAccount(page, accountId);
			cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page",
					(String) response.get("page"), 390);
			//response.put("sessionId", sessionId);
			response.remove("page");
			response.remove("accounts");
			return response;
		} else {
			cassandra.deleteData(CassandraService.CF_SESSIONS, sessionId);
			HashMap<String, Object> r = new HashMap<String, Object>();
			r.put("error", "Session has expired");
			return r;
		}
	}

}