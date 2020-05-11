package de.l3s.interwebj.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.jaxb.services.ServicesResponse;
import org.junit.jupiter.api.Test;

import static de.l3s.interwebj.rest.Endpoint.createWebResource;
import static org.junit.jupiter.api.Assertions.*;

class ServicesTest {
    @Test
    void testServices() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        //		AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***",
        //		"***REMOVED***");
        AuthCredentials userCredentials = null;
        //		userCredentials = new AuthCredentials("***REMOVED***",
        //		                                      "***REMOVED***");
        WebResource resource = createWebResource("http://localhost:8181/InterWebJ/api/services", consumerCredentials, userCredentials);
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.get(ClientResponse.class);
        ServicesResponse servicesResponse = response.getEntity(ServicesResponse.class);
        System.out.println(servicesResponse);
    }
}