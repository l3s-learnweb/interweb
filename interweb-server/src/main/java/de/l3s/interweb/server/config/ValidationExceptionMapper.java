package de.l3s.interweb.server.config;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationExceptionMapper;
import io.quarkus.hibernate.validator.runtime.jaxrs.ViolationReport;

@Provider
public class ValidationExceptionMapper extends ResteasyReactiveViolationExceptionMapper implements ExceptionMapper<ValidationException> {
    private static final String VALIDATION_HEADER = "validation-exception";

    @Override
    public Response toResponse(ValidationException exception) {
        try {
            return super.toResponse(exception);
        } catch (ValidationException vex) {
            Response.Status status = Response.Status.BAD_REQUEST;
            Response.ResponseBuilder builder = Response.status(status);
            builder.header(VALIDATION_HEADER, "true");

            List<ViolationReport.Violation> violationsInReport = new ArrayList<>(1);
            violationsInReport.add(new ViolationReport.Violation(null, vex.getMessage()));
            builder.entity(new ViolationReport("Constraint Violation", status, violationsInReport));
            builder.type(MediaType.APPLICATION_JSON_TYPE);

            return builder.build();
        }
    }
}
