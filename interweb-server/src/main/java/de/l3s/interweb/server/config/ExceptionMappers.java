package de.l3s.interweb.server.config;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

 import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {
    private static final Logger log = Logger.getLogger(ExceptionMappers.class);

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(NotFoundException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, ErrorResponse.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(NoResultException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, ErrorResponse.of("The requested entity was not found."));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(BadRequestException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, ErrorResponse.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(Exception x) {
        log.error("Unexpected error", x);
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, ErrorResponse.of(x));
    }

}
