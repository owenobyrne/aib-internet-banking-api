// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17/03/2013 01:04:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AccountTransactionsResource.java

package com.owenobyrne.aibibs.resource;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
public class AccountTransactionsResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@Autowired
	AibInternetBankingService aibibs;
	@Autowired
	CassandraService cassandra;

	@Path("/{accountId}/transactions")
	@POST
	@Consumes(value="application/x-www-form-urlencoded")
	@Produces(value="application/json")
	public HashMap<String, Object> getTransactions(@PathParam(value = "accountId") String accountId,
			MultivaluedMap<String, String> params) {
		
		String sessionId = params.getFirst("SESSION_ID");
		String page = cassandra.getData(CassandraService.CF_SESSIONS,
				sessionId, "page");
		if (page != null) {
			HashMap<String, Object> response = aibibs
					.getTransactionsForAccount(page, accountId);
			cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page",
					(String) response.get("page"), 180);
			response.put("sessionId", sessionId);
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