package de.l3s.interweb.server.config;

import javax.naming.LimitExceededException;

import jakarta.persistence.NoResultException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.*;
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
    public RestResponse<ErrorResponse> mapException(de.l3s.interweb.core.NoResultException x) {
        return RestResponse.status(Response.Status.NO_CONTENT, ErrorResponse.of("No results found."));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(BadRequestException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, ErrorResponse.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(WebApplicationException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, ErrorResponse.of(x.getCause()));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(ForbiddenException x) {
        return RestResponse.status(Response.Status.FORBIDDEN, ErrorResponse.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(NotAllowedException x) {
        return RestResponse.status(Response.Status.METHOD_NOT_ALLOWED, ErrorResponse.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(NotSupportedException x) {
        return RestResponse.status(Response.Status.UNSUPPORTED_MEDIA_TYPE, ErrorResponse.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(ValidationException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, ErrorResponse.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(LimitExceededException x) {
        return RestResponse.status(Response.Status.PAYMENT_REQUIRED, ErrorResponse.of("Monthly number of paid requests exceeded. Please contact L3S support."));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapException(Exception x) {
        log.error("Unexpected error", x);
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, ErrorResponse.of(x));
    }

}
