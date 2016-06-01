package com.aote.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

@Path("error")
@Component
public class ErrorService {
	@GET
	public String getError() {
		return "afsdd";
	}
}
