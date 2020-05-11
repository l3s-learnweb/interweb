package de.l3s.interwebj.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.jaxb.EmbeddedResponse;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddedTest {

    @Test
    void getEmbedded() {
        AuthCredentials consumerCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        AuthCredentials userCredentials = new AuthCredentials("***REMOVED***", "***REMOVED***");
        //		AuthCredentials userCredentials = new AuthCredentials("***REMOVED***",
        //		                                                      "***REMOVED***");
        WebResource resource = Endpoint.createWebResource("http://localhost:8181/InterWebJ/api/embedded", consumerCredentials, userCredentials);
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("url", "http://flickr.com/photos/35948364@N00/330342884");
        //		params.add("url",
        //		           "http://www.youtube.com/watch?v=-hRycGcj_AQ&feature=youtube_gdata_player");
        params.add("max_width", "100");
        params.add("max_height", "100");
        System.out.println("querying InterWebJ URL: " + resource.toString());
        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, params);
        EmbeddedResponse embeddedResponse = response.getEntity(EmbeddedResponse.class);
        System.out.println(embeddedResponse);
    }
}