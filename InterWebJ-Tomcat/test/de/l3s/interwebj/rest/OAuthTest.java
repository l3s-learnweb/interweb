package de.l3s.interwebj.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.jaxb.SearchResponse;
import de.l3s.interwebj.jaxb.auth.OAuthAccessTokenResponse;
import de.l3s.interwebj.jaxb.auth.OAuthRequestTokenResponse;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import static de.l3s.interwebj.rest.Endpoint.createWebResource;
import static org.junit.jupiter.api.Assertions.*;

class OAuthTest {

    @Test
    void testAddUser() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/oauth/register", consumerCredentials, null);
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("username", "user2");
        params.add("password", "123456");
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, params);
        OAuthAccessTokenResponse accessTokenResponse = response.getEntity(OAuthAccessTokenResponse.class);
        System.out.println(accessTokenResponse);
    }

    @Test
    void testOauthAuthentication() throws IOException {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/oauth/OAuthGetRequestToken", consumerCredentials, null);
        System.out.println("querying InterWebJ request token: " + resource.toString());
        ClientResponse response = resource.get(ClientResponse.class);
        OAuthRequestTokenResponse requestTokenResponse = response.getEntity(OAuthRequestTokenResponse.class);
        System.out.println(requestTokenResponse);
        String tokenKey = requestTokenResponse.getRequestToken().getOauthToken();
        String tokenSecret = requestTokenResponse.getRequestToken().getOauthTokenSecret();
        AuthCredentials requestTokenAuthCredentials = new AuthCredentials(tokenKey, tokenSecret);
        URI authorizationUri = URI.create("http://localhost:8181/InterWebJ/api/oauth/OAuthAuthorizeToken" + "?oauth_token=" + tokenKey
                //		                                  + "&oauth_callback=http://localhost:8181/InterWebJ/view/search.xhtml"
        );
        System.out.println("authorize token url: " + authorizationUri.toASCIIString());
        Desktop.getDesktop().browse(authorizationUri);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter access token:");
        String accessToken = in.readLine();
        System.out.println("Enter access token secret:");
        String accessTokenSecret = in.readLine();
        AuthCredentials accessTokenAuthCredentials = new AuthCredentials(accessToken, accessTokenSecret);
        System.out.println(accessTokenAuthCredentials);

        resource = createWebResource("http://localhost:8181/InterWebJ/api/oauth/OAuthGetAccessToken", consumerCredentials, accessTokenAuthCredentials);
        System.out.println("querying InterWebJ access token: " + resource.toString());
        response = resource.get(ClientResponse.class);
        OAuthAccessTokenResponse accessTokenResponse = response.getEntity(OAuthAccessTokenResponse.class);
        System.out.println(accessTokenResponse);
        accessToken = accessTokenResponse.getAccessToken().getOauthToken();
        accessTokenSecret = accessTokenResponse.getAccessToken().getOauthTokenSecret();
        System.out.println("accessToken: [" + accessToken + "]");
        System.out.println("accessTokenSecret: [" + accessTokenSecret + "]");
    }

    @Test
    void testSearch() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/search", consumerCredentials, userCredentials);
        resource = resource.queryParam("q", "people");
        resource = resource.queryParam("media_types", "image,video,text,audio");
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.get(ClientResponse.class);
        SearchResponse searchResponse = response.getEntity(SearchResponse.class);
        System.out.println(searchResponse);
    }
}