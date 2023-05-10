package org.isobit.app;

import java.util.HashMap;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AppExceptionMapper implements ExceptionMapper<Throwable> {
  
	@Override
	public Response toResponse(Throwable arg0) {
		HashMap m=new HashMap();
		arg0.printStackTrace();
		m.put("msg", arg0.getMessage());
		return Response
			.status(Response.Status.INTERNAL_SERVER_ERROR)
			.entity(m)
			.build();
	}
  //.contentType(MediaType.APPLICATION_OCTET_STREAM)
}
