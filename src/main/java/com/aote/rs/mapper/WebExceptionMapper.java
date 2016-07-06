package com.aote.rs.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebExceptionMapper implements ExceptionMapper<WebException> {
	public Response toResponse(WebException ex) {
	    return Response.status(ex.status).
	      entity(ex.getMessage()).
	      type("text/plain").
	      build();
	}
}
