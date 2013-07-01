package com.owenobyrne.aibibs.resource;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eaio.uuid.UUID;
import com.owenobyrne.aibibs.services.AibInternetBankingService;
import com.owenobyrne.aibibs.services.CassandraService;

@Path("/login")
@Component
public class LoginResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	@Autowired
	AibInternetBankingService aibibs;
	@Autowired
	CassandraService cassandra;
	
	@Path("/registration")
	@POST
	@Consumes(value="application/x-www-form-urlencoded")
	@Produces(value="application/json")
	public HashMap<String, Object> enterRegistrationNumber(
			MultivaluedMap<String, String> registrationParams) {
		String registrationNumber = registrationParams
				.getFirst("REGISTRATION_NUMBER");
		HashMap<String, Object> response = aibibs.enterRegistrationNumber(registrationNumber);
		UUID sessionId = new UUID();
		cassandra.addData(CassandraService.CF_SESSIONS, sessionId.toString(),
				"page", (String) response.get("page"), 180);
		response.put("sessionId", sessionId.toString());
		response.remove("page");
		return response;
	}

	@Path("/pac")
	@POST
	@Consumes(value="application/x-www-form-urlencoded")
	@Produces(value="application/json")
	public HashMap<String, Object> enterPACDigits(
			@QueryParam("sessionId") String sessionId,
			MultivaluedMap<String, String> pacParams) {
		String pac1 = (String) pacParams.getFirst("PAC1");
		String pac2 = (String) pacParams.getFirst("PAC2");
		String pac3 = (String) pacParams.getFirst("PAC3");
		String digits = (String) pacParams.getFirst("DIGITS");
		//String sessionId = (String) pacParams.getFirst("SESSION_ID");
		String page = cassandra.getData(CassandraService.CF_SESSIONS,sessionId, "page");
		if (page != null) {
			HashMap<String, Object> response = aibibs.enterPACDigits(page, pac1, pac2, pac3, digits);
			cassandra.addData(CassandraService.CF_SESSIONS, sessionId, "page", (String) response.get("page"), 180);
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

}