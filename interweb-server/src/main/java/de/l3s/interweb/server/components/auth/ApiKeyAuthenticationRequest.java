package de.l3s.interweb.server.components.auth;

import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.BaseAuthenticationRequest;

public class ApiKeyAuthenticationRequest extends BaseAuthenticationRequest implements AuthenticationRequest {
    private final String value;

    public ApiKeyAuthenticationRequest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
