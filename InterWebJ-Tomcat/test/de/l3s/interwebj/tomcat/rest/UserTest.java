package de.l3s.interwebj.tomcat.rest;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.tomcat.TestUtils;
import de.l3s.interwebj.tomcat.jaxb.services.AuthorizationLinkResponse;
import de.l3s.interwebj.tomcat.jaxb.services.ServiceResponse;
import de.l3s.interwebj.tomcat.jaxb.services.ServicesResponse;

class UserTest {

    @Test
    void testAuthService() throws IOException {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebTarget resource = TestUtils.createWebTarget("api/users/default/services/" + "InterWeb" + "/auth", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().post(Entity.json(null));
        AuthorizationLinkResponse authorizationLinkResponse = response.readEntity(AuthorizationLinkResponse.class);
        System.out.println(authorizationLinkResponse);
        String location = authorizationLinkResponse.getAuthorizationLinkEntity().getLink();
        System.out.println("redirecting to: [" + location + "]");
        Desktop.getDesktop().browse(URI.create(location));
        System.out.println(authorizationLinkResponse);
    }

    @Test
    void testRemoveMediator() {
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebTarget resource = TestUtils.createWebTarget("api/users/default/mediator", userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().delete();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testRevokeService() {
        WebTarget resource = TestUtils.createWebTarget("api/users/default/services/" + "youtube" + "/auth");
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().delete();
        ServiceResponse serviceResponse = response.readEntity(ServiceResponse.class);
        System.out.println(serviceResponse);
    }

    @Test
    void testSetMediator() {
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebTarget resource = TestUtils.createWebTarget("api/users/default/mediator", userCredentials);
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("mediator_token", TestUtils.userCredentials.getKey());
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().post(Entity.form(params));
        assertEquals(201, response.getStatus());
    }

    @Test
    void testUserInfo() {
        WebTarget resource = TestUtils.createWebTarget("api/users/default");
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().get();
        String responseContent = response.readEntity(String.class);
        System.out.println(responseContent);
    }

    @Test
    void testUserService() {
        WebTarget resource = TestUtils.createWebTarget("api/users/default/services/" + "flickr");
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().get();
        ServiceResponse serviceResponse = response.readEntity(ServiceResponse.class);
        System.out.println(serviceResponse);
    }

    @Test
    void testUserServices() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        // AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebTarget resource = TestUtils.createWebTarget("api/users/default/services", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().get();
        ServicesResponse servicesResponse = response.readEntity(ServicesResponse.class);
        System.out.println(servicesResponse);
    }
}
