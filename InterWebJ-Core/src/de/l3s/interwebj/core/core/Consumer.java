package de.l3s.interwebj.core.core;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import de.l3s.interwebj.core.AuthCredentials;

public record Consumer(String name, String url, String description, AuthCredentials authCredentials) {
    public Consumer {
        notNull(name, "name");
        notNull(authCredentials, "authCredentials");
    }
}
