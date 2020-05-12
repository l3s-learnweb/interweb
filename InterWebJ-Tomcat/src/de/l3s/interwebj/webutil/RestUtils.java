package de.l3s.interwebj.webutil;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.l3s.interwebj.jaxb.ErrorResponse;

public class RestUtils
{
    public static void throwWebApplicationException(ErrorResponse errorResponse)
    {
        Response response = Response.ok(errorResponse, MediaType.APPLICATION_XML).build();
        throw new WebApplicationException(response);
    }
}
