package de.l3s.interweb.core;

import java.io.Serial;
import java.io.Serializable;

import de.l3s.interweb.core.util.Assertions;

public class AuthCredentials implements Serializable {
    @Serial
    private static final long serialVersionUID = 1411969017572131214L;

    private final String key;
    private final String secret;

    public AuthCredentials(String key) {
        this(key, null);
    }

    public AuthCredentials(String key, String secret) {
        Assertions.notNull(key, "key");
        Assertions.notEmpty(key, "key");

        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }
}
