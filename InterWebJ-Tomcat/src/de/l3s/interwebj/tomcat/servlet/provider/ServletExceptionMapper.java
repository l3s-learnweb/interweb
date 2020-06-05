package de.l3s.interwebj.tomcat.servlet.provider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class ServletExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger log = LogManager.getLogger(ServletExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof WebApplicationException) {
            return Response.fromResponse(((WebApplicationException) e).getResponse()).entity(e.getMessage()).build();
        }

        log.error("Fatal error during processing request", e);
        return Response.serverError().entity(e.getMessage()).build();
    }
}
