package de.l3s.interweb.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeMap;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.l3s.interweb.core.search.SearchConnectorResults;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.search.SearchResults;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@QuarkusTest
class InterwebTest {
    @ConfigProperty(name = "interweb.server")
    String server;

    @ConfigProperty(name = "interweb.apikey")
    String apikey;

    @Test
    void simpleTest() throws InterwebException {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("q", "spacex");
        params.put("language", "en");
        params.put("content_types", "video");
        params.put("services", "YouTube,Vimeo");
        params.put("per_page", "32");
        params.put("page", "1");
        params.put("extras", "duration,tags");
        params.put("timeout", "50");

        Interweb iw = new Interweb(server, apikey);

        SearchResults response = iw.search(params);
        assertEquals(response.getResults().size(), 2);

        for (SearchConnectorResults result : response.getResults()) {
            assertTrue(result.getTotalResults() > 0);

            for (SearchItem item : result.getItems()) {
                System.out.println(item.getTitle() + " [" + item.getDuration() + "]");
                System.out.println(item.getUrl());
            }
        }
    }
}
