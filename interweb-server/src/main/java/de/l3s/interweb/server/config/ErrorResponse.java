package de.l3s.interweb.server.config;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public final class ErrorResponse {
    private String details;

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return details;
    }

    public static ErrorResponse of(String message) {
        ErrorResponse error = new ErrorResponse();
        error.details = message;
        return error;
    }

    public static ErrorResponse of(Throwable e) {
        return of(e.getMessage());
    }
}
