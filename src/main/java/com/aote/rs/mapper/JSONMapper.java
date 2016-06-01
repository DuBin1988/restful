package com.aote.rs.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JSONMapper implements ExceptionMapper<RuntimeException> {
	public Response toResponse(RuntimeException ex) {
	    return Response.status(500).
	      entity(ex.getMessage()).
	      type("text/plain").
	      build();
	}
}
