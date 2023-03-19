package de.l3s.interweb.tomcat.app;

import static de.l3s.interweb.core.util.Assertions.notNull;

import de.l3s.interweb.core.AuthCredentials;

public record Consumer(String name, String url, String description, AuthCredentials authCredentials) {
    public Consumer {
        notNull(name, "name");
        notNull(authCredentials, "authCredentials");
    }
}
