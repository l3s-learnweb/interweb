package de.l3s.interwebj.core.core;

import static de.l3s.interwebj.core.util.Assertions.notNull;

import de.l3s.interwebj.core.AuthCredentials;

public class Consumer {

    private final String name;
    private final String url;
    private final String description;
    private final AuthCredentials authCredentials;

    public Consumer(String name, String url, String description, AuthCredentials authCredentials) {
        notNull(name, "name");
        notNull(authCredentials, "authCredentials");
        this.name = name;
        this.url = url;
        this.description = description;
        this.authCredentials = authCredentials;
    }

    public AuthCredentials getAuthCredentials() {
        return authCredentials;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
