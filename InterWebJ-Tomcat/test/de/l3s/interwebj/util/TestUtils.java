package de.l3s.interwebj.util;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.rest.Endpoint;

import javax.ws.rs.client.WebTarget;

public final class TestUtils {
    public static final String serverUrl = "http://localhost:8080/InterWebJ/";
    public static final AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
    public static final AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
    
    public static WebTarget createWebTarget(final String path)
    {
        return Endpoint.createWebTarget(serverUrl + path, consumerCredentials, userCredentials);
    }

    public static WebTarget createWebTarget(final String path, AuthCredentials userCredentials)
    {
        return Endpoint.createWebTarget(serverUrl + path, consumerCredentials, userCredentials);
    }

    public static WebTarget createWebTarget(final String path, AuthCredentials consumerCredentials, AuthCredentials userCredentials)
    {
        return Endpoint.createWebTarget(serverUrl + path, consumerCredentials, userCredentials);
    }
}
