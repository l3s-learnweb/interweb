package de.l3s.interwebj.webutil;


import javax.ws.rs.*;
import javax.ws.rs.core.*;

import de.l3s.interwebj.jaxb.*;


public class RestUtils
{
	
	public static void throwWebApplicationException(ErrorResponse errorResponse)
	{
		Response response = Response.ok(errorResponse,
		                                MediaType.APPLICATION_XML).build();
		throw new WebApplicationException(response);
	}
	
}
