package de.l3s.interweb.tomcat.rest;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1Builder;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.oauth1.signature.OAuth1Parameters;
import org.glassfish.jersey.server.oauth1.internal.OAuthServerRequest;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.tomcat.core.Environment;
import de.l3s.interweb.tomcat.core.InterWebPrincipal;
import de.l3s.interweb.tomcat.db.Database;

public class Endpoint {
    @Context
    private UriInfo info;

    @Context
    private ContainerRequestContext requestContext;

    public static WebTarget createWebTarget(String url, AuthCredentials consumerCredentials, AuthCredentials userCredentials) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);

        ConsumerCredentials consumerCred = new ConsumerCredentials(consumerCredentials.getKey(), consumerCredentials.getSecret());

        OAuth1Builder.FilterFeatureBuilder filterFeature = OAuth1ClientSupport.builder(consumerCred).feature();

        if (userCredentials != null) {
            AccessToken storedToken = new AccessToken(userCredentials.getKey(), userCredentials.getSecret());
            filterFeature.accessToken(storedToken);
        }
        target.register(filterFeature.build());

        return target;
    }

    public URI getBaseUri() {
        return info.getBaseUri();
    }

    public OAuth1Parameters getOAuthParameters() {
        OAuthServerRequest osr = new OAuthServerRequest(requestContext);
        return new OAuth1Parameters().readRequest(osr);
    }

    public InterWebPrincipal getPrincipal() {
        OAuth1Parameters params = getOAuthParameters();
        String token = params.getToken();
        if (token == null) {
            return null;
        }
        Database database = Environment.getInstance().getDatabase();
        return database.readPrincipalByKey(token);
    }
}
