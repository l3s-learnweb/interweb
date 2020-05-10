package de.l3s.interwebj;

import de.l3s.interwebj.jaxb.SearchResponse;
import de.l3s.interwebj.jaxb.SearchResultEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class InterWebTest {
    // private static final String SERVER_URL = "http://localhost:8080/InterWebJ/api/";
    private static final String SERVER_URL = "http://learnweb.l3s.uni-hannover.de:11080/interweb/api/";
    private static final String CONSUMER_KEY = "***REMOVED***";
    private static final String CONSUMER_SECRET = "***REMOVED***";
    
    @Test
    void simpleTest() throws IOException, IllegalResponseException {
        TreeMap<String, String> params = new TreeMap<>();

        params.put("media_types", "text"); // ,image
        params.put("services", "Bing"); // "YouTube,Vimeo"
        params.put("number_of_results", "10");
        params.put("page", "1");
        params.put("language", "de");

        InterWeb iw = new InterWeb(SERVER_URL, CONSUMER_KEY, CONSUMER_SECRET);

        SearchResponse response = iw.search("london", params);
        for(SearchResultEntity result :  response.getQuery().getResults())
        {
            System.out.println(result.getTitle());
            System.out.println(result.getUrl());
        }
    }
}