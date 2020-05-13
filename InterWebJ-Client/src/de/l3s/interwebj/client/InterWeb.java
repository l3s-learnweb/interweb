package de.l3s.interwebj.client;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interwebj.client.jaxb.SearchResponse;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response;

public class InterWeb implements Serializable
{
    private static final Logger log = LogManager.getLogger(InterWeb.class);
    private static final long serialVersionUID = -1621494088505203391L;

    private final String consumerKey;
    private final String consumerSecret;
    private final String interwebApiURL;

    public InterWeb(String interwebApiURL, String consumerKey, String consumerSecret)
    {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.interwebApiURL = interwebApiURL;
    }

    private WebTarget createWebTarget(final String apiPath)
    {
        final String apiUrl = interwebApiURL + apiPath;
        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(apiUrl);

        Feature filterFeature = OAuth1ClientSupport.builder(consumerCredentials).feature().build();
        target.register(filterFeature);

        return target;
    }

    /**
     * @param query  The query string
     * @param params see http://athena.l3s.uni-hannover.de:8000/doc/search
     */
    public SearchResponse search(String query, TreeMap<String, String> params)
    {
        if(null == query || query.isEmpty())
        {
            throw new IllegalArgumentException("empty query");
        }

        WebTarget target = createWebTarget("search");
        target = target.queryParam("q", query);
        for(final Map.Entry<String, String> entry : params.entrySet())
        {
            String value = entry.getValue();
            target = target.queryParam(entry.getKey(), value);
        }

        Response response = target.request().get();

        if(response.getStatus() != 200)
        {
            String content = response.readEntity(String.class);

            log.fatal("Interweb request failed; Error code: " + response.getStatus() + "; for query:" + query + " | " + params + "; response: " + content);
            throw new RuntimeException("Interweb request failed : HTTP error code : " + response.getStatus());
        }

        return response.readEntity(SearchResponse.class);
    }
}
