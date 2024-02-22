package de.l3s.interweb.connector.bing.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class ErrorResponse {

    @JsonProperty("error")
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
