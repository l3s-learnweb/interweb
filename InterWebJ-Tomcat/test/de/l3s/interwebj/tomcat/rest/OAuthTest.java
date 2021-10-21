package de.l3s.interwebj.tomcat.rest;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.query.SearchResults;
import de.l3s.interwebj.tomcat.TestUtils;
import de.l3s.interwebj.tomcat.jaxb.auth.OAuthAccessTokenResponse;
import de.l3s.interwebj.tomcat.jaxb.auth.OAuthRequestTokenResponse;

class OAuthTest {

    @Test
    void testAddUser() {
        WebTarget resource = TestUtils.createWebTarget("api/oauth/register", null);

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("username", "testuser2020");
        params.add("password", "123456");
        System.out.println("querying InterWebJ URL: " + resource);

        Response response = resource.request().post(Entity.form(params));
        OAuthAccessTokenResponse accessTokenResponse = response.readEntity(OAuthAccessTokenResponse.class);
        System.out.println(accessTokenResponse);
    }

    @Test
    void testOauthAuthentication() throws IOException {
        WebTarget resource = TestUtils.createWebTarget("api/oauth/OAuthGetRequestToken", null);
        System.out.println("querying InterWebJ request token: " + resource);

        Response response = resource.request().get();
        OAuthRequestTokenResponse requestTokenResponse = response.readEntity(OAuthRequestTokenResponse.class);
        System.out.println(requestTokenResponse);
        String tokenKey = requestTokenResponse.getRequestToken().getOauthToken();
        // + "&oauth_callback=http://localhost:8080/InterWebJ/view/search.xhtml"
        URI authorizationUri = URI.create(TestUtils.serverUrl + "api/oauth/OAuthAuthorizeToken" + "?oauth_token=" + tokenKey);
        System.out.println("authorize token url: " + authorizationUri.toASCIIString());
        Desktop.getDesktop().browse(authorizationUri);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("Enter access token:");
        String accessToken = in.readLine();
        System.out.println("Enter access token secret:");
        String accessTokenSecret = in.readLine();
        AuthCredentials accessTokenAuthCredentials = new AuthCredentials(accessToken, accessTokenSecret);
        System.out.println(accessTokenAuthCredentials);

        resource = TestUtils.createWebTarget("api/oauth/OAuthGetAccessToken", accessTokenAuthCredentials);
        System.out.println("querying InterWebJ access token: " + resource);
        response = resource.request().get();
        OAuthAccessTokenResponse accessTokenResponse = response.readEntity(OAuthAccessTokenResponse.class);
        System.out.println(accessTokenResponse);
        accessToken = accessTokenResponse.getAccessToken().getOauthToken();
        accessTokenSecret = accessTokenResponse.getAccessToken().getOauthTokenSecret();
        System.out.println("accessToken: [" + accessToken + "]");
        System.out.println("accessTokenSecret: [" + accessTokenSecret + "]");
    }

    @Test
    void testSearch() {
        WebTarget resource = TestUtils.createWebTarget("api/search");
        resource = resource.queryParam("q", "people");
        resource = resource.queryParam("media_types", "image,video,text");
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().get();
        SearchResults searchResponse = response.readEntity(SearchResults.class);
        System.out.println(searchResponse);
    }
}
