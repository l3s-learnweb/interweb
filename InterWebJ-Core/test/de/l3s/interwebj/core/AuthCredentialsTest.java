package de.l3s.interwebj.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthCredentialsTest {

    @Test
    void random() {
        AuthCredentials authCredentials = AuthCredentials.random();
        AuthCredentials nextCredentials = AuthCredentials.random();

        assertNotEquals(authCredentials.getKey(), authCredentials.getSecret());
        assertNotEquals(nextCredentials.getKey(), nextCredentials.getSecret());
        assertNotEquals(authCredentials.getKey(), nextCredentials.getKey());
        assertNotEquals(authCredentials.getSecret(), nextCredentials.getSecret());
    }
}
