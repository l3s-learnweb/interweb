package de.l3s.interwebj.rest;

import de.l3s.interwebj.jaxb.EmbeddedResponse;
import de.l3s.interwebj.util.TestUtils;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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
        System.out.println("querying InterWebJ URL: " + target.toString());

        Response response = target.request().post(Entity.form(params));
        EmbeddedResponse embeddedResponse = response.readEntity(EmbeddedResponse.class);
        System.out.println(embeddedResponse);
    }
}