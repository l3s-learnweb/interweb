package de.l3s.interwebj.tomcat.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.l3s.interwebj.tomcat.TestUtils;
import de.l3s.interwebj.tomcat.jaxb.EmbeddedResponse;

class EmbeddedTest {

    @Test
    void getEmbedded() {
        WebTarget target = TestUtils.createWebTarget("api/embedded", null);

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        // params.add("url", "http://flickr.com/photos/35948364@N00/330342884");
        // params.add("url", "http://www.youtube.com/watch?v=-hRycGcj_AQ&feature=youtube_gdata_player");
        params.add("url", "https://www.slideshare.net/pacific2000/flowers-presentation-715934");
        params.add("max_width", "100");
        params.add("max_height", "100");
        System.out.println("querying InterWebJ URL: " + target);

        Response response = target.request().post(Entity.form(params));
        EmbeddedResponse embeddedResponse = response.readEntity(EmbeddedResponse.class);
        System.out.println(embeddedResponse);
    }
}
