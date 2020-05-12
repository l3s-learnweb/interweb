package de.l3s.interwebj.rest;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.jaxb.OkResponse;
import de.l3s.interwebj.jaxb.services.AuthorizationLinkResponse;
import de.l3s.interwebj.jaxb.services.ServiceResponse;
import de.l3s.interwebj.jaxb.services.ServicesResponse;
import de.l3s.interwebj.util.TestUtils;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

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
        OkResponse servicesResponse = response.readEntity(OkResponse.class);
        System.out.println(servicesResponse);
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
        OkResponse servicesResponse = response.readEntity(OkResponse.class);
        System.out.println(servicesResponse);
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
        //		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
        //		                                                      "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebTarget resource = TestUtils.createWebTarget("api/users/default/services", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        Response response = resource.request().get();
        ServicesResponse servicesResponse = response.readEntity(ServicesResponse.class);
        System.out.println(servicesResponse);
    }
}