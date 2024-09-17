package de.l3s.interweb.server.config;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {
    @ServerExceptionMapper
    public RestResponse<HttpError> mapException(Exception x) {
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, HttpError.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<HttpError> mapException(NotFoundException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, HttpError.of(x));
    }

    @ServerExceptionMapper
    public RestResponse<HttpError> mapException(BadRequestException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, HttpError.of(x));
    }

    public static final class HttpError {
        private String message;

        public String getMessage() {
            return message;
        }

        public static HttpError of(Exception e) {
            HttpError error = new HttpError();
            error.message = e.getMessage();
            return error;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
