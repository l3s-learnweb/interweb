package de.l3s.interwebj.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.jaxb.OkResponse;
import de.l3s.interwebj.jaxb.services.AuthorizationLinkResponse;
import de.l3s.interwebj.jaxb.services.ServiceResponse;
import de.l3s.interwebj.jaxb.services.ServicesResponse;
import de.l3s.interwebj.util.CoreUtils;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

import static de.l3s.interwebj.rest.Endpoint.createWebResource;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testAuthService() throws IOException {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services/" + "InterWeb" + "/auth", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.post(ClientResponse.class);
        AuthorizationLinkResponse authorizationLinkResponse = response.getEntity(AuthorizationLinkResponse.class);
        System.out.println(authorizationLinkResponse);
        String location = authorizationLinkResponse.getAuthorizationLinkEntity().getLink();
        System.out.println("redirecting to: [" + location + "]");
        Desktop.getDesktop().browse(URI.create(location));
        System.out.println(authorizationLinkResponse);
    }

    @Test
    void testRemoveMediator() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials mediatorCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/mediator", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.delete(ClientResponse.class);
        OkResponse servicesResponse = response.getEntity(OkResponse.class);
        System.out.println(servicesResponse);
    }

    @Test
    void testRevokeService() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services/" + "youtube" + "/auth", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.delete(ClientResponse.class);
        ServiceResponse serviceResponse = response.getEntity(ServiceResponse.class);
        System.out.println(serviceResponse);
    }

    @Test
    void testSetMediator() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials mediatorCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/mediator", consumerCredentials, userCredentials);
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("mediator_token", mediatorCredentials.getKey());
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, params);
        OkResponse servicesResponse = response.getEntity(OkResponse.class);
        System.out.println(servicesResponse);
    }

    @Test
    void testUserInfo() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.get(ClientResponse.class);
        String responseContent = response.getEntity(String.class);
        System.out.println(responseContent);
    }

    @Test
    void testUserService() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services/" + "flickr", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.get(ClientResponse.class);
        ServiceResponse serviceResponse = response.getEntity(ServiceResponse.class);
        System.out.println(serviceResponse);
    }

    @Test
    void testUserServices() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        //		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
        //		                                                      "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/users/default/services", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.get(ClientResponse.class);
        ServicesResponse servicesResponse = response.getEntity(ServicesResponse.class);
        System.out.println(servicesResponse);
    }
}