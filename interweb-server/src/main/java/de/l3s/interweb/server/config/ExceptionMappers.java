package de.l3s.interweb.server.config;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {
    @ServerExceptionMapper
    public RestResponse<String> mapValidationException(ValidationException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, x.getMessage());
    }
}
