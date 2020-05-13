package de.l3s.interwebj.client;

import de.l3s.interwebj.client.InterWeb;
import de.l3s.interwebj.client.jaxb.SearchResponse;
import de.l3s.interwebj.client.jaxb.SearchResultEntity;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class InterWebTest {
    // private static final String SERVER_URL = "http://localhost:8080/InterWebJ/api/";
    private static final String SERVER_URL = "http://learnweb.l3s.uni-hannover.de:11080/interweb/api/";
    private static final String CONSUMER_KEY = "***REMOVED***";
    private static final String CONSUMER_SECRET = "***REMOVED***";
    
    @Test
    void simpleTest() {
        TreeMap<String, String> params = new TreeMap<>();

        params.put("media_types", "video");
        params.put("services", "YouTube");
        params.put("number_of_results", "10");
        params.put("page", "1");
        params.put("language", "de");

        InterWeb iw = new InterWeb(SERVER_URL, CONSUMER_KEY, CONSUMER_SECRET);

        SearchResponse response = iw.search("london", params);
        assertTrue(response.getQuery().getResults().size() > 0);

        for(SearchResultEntity result :  response.getQuery().getResults())
        {
            System.out.println(result.getTitle());
            System.out.println(result.getUrl());
        }
    }
}