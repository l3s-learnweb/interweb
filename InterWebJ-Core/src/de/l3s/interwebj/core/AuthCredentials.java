package de.l3s.interwebj.core;

import static de.l3s.interwebj.core.util.Assertions.notEmpty;
import static de.l3s.interwebj.core.util.Assertions.notNull;

import java.io.Serial;
import java.io.Serializable;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AuthCredentials implements Serializable {
    @Serial
    private static final long serialVersionUID = 1411969017572131214L;

    private final String key;
    private final String secret;

    public static AuthCredentials random() {
        String key = RandomStringUtils.randomAlphanumeric(16);
        String secret = RandomStringUtils.randomAlphanumeric(24);
        return new AuthCredentials(key, secret);
    }

    public AuthCredentials(String key) {
        this(key, null);
    }

    public AuthCredentials(String key, String secret) {
        notNull(key, "key");
        notEmpty(key, "key");

        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("key", key)
            .append("secret", secret)
            .toString();
    }
}
