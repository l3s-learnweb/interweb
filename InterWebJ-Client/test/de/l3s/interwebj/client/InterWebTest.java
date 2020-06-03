package de.l3s.interwebj.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import de.l3s.interwebj.client.model.SearchResponse;
import de.l3s.interwebj.client.model.SearchResult;

class InterWebTest {
    // private static final String SERVER_URL = "http://localhost:8080/InterWebJ/api/";
    private static final String SERVER_URL = "***REMOVED***/api/";
    private static final String CONSUMER_KEY = "***REMOVED***";
    private static final String CONSUMER_SECRET = "***REMOVED***";

    @Test
    void simpleTest() {
        TreeMap<String, String> params = new TreeMap<>();

        params.put("language", "en");
        params.put("media_types", "video");
        params.put("services", "YouTube,Vimeo");
        params.put("number_of_results", "32");
        params.put("page", "1");
        params.put("timeout", "50");

        InterWeb iw = new InterWeb(SERVER_URL, CONSUMER_KEY, CONSUMER_SECRET);

        SearchResponse response = iw.search("spacex", params);
        assertTrue(response.getQuery().getResults().size() > 0);
        assertTrue(response.getQuery().getFacetSources().get("YouTube") > 0);
        assertTrue(response.getQuery().getFacetSources().get("Vimeo") > 0);

        for (SearchResult result : response.getQuery().getResults()) {
            System.out.println(result.getTitle());
            System.out.println(result.getUrl());
        }
    }
}
