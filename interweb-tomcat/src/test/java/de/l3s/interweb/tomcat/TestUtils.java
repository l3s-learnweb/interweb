package de.l3s.interweb.tomcat;

import jakarta.ws.rs.client.WebTarget;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.tomcat.rest.Endpoint;

public final class TestUtils {
    public static final String serverUrl = "http://localhost:8080/interweb/";
    public static final AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
    public static final AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");

    public static WebTarget createWebTarget(final String path) {
        return Endpoint.createWebTarget(serverUrl + path, consumerCredentials, userCredentials);
    }

    public static WebTarget createWebTarget(final String path, AuthCredentials userCredentials) {
        return Endpoint.createWebTarget(serverUrl + path, consumerCredentials, userCredentials);
    }

    public static WebTarget createWebTarget(final String path, AuthCredentials consumerCredentials, AuthCredentials userCredentials) {
        return Endpoint.createWebTarget(serverUrl + path, consumerCredentials, userCredentials);
    }
}
