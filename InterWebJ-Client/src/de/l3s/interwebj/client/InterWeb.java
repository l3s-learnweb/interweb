package de.l3s.interwebj.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.l3s.interwebj.client.model.SearchResponse;

public class InterWeb implements Serializable {
    private static final long serialVersionUID = 7231324400348062196L;
    private static final Logger log = LogManager.getLogger(InterWeb.class);

    private final String interwebApiURL;
    private final String consumerKey;
    private final String consumerSecret;

    public InterWeb(String interwebApiURL, String consumerKey, String consumerSecret) {
        this.interwebApiURL = interwebApiURL;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    private URI createRequestUri(final String apiPath, final TreeMap<String, String> params) {
        StringBuilder sb = new StringBuilder();

        sb.append(interwebApiURL);
        if (sb.charAt(sb.length() - 1) != '/') {
            sb.append('/');
        }
        sb.append(apiPath);

        boolean isFirst = true;
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(isFirst ? '?' : '&').append(entry.getKey()).append('=').append(OAuth1SignatureBuilder.percentEncode(entry.getValue()));
            isFirst = false;
        }

        return URI.create(sb.toString());
    }

    private HttpRequest createRequest(final String apiPath, final TreeMap<String, String> params) {
        final URI apiUri = createRequestUri(apiPath, params);

        String authorizationHeader = new OAuth1SignatureBuilder()
            .withURI(apiUri).withConsumerKey(consumerKey).withConsumerSecret(consumerSecret)
            .build();

        return HttpRequest.newBuilder().GET()
            .uri(apiUri)
            .header("Accept", "application/json")
            .header("Authorization", authorizationHeader)
            .build();
    }

    public SearchResponse search(TreeMap<String, String> params) {
        String query = params.get("q");
        if (null == query || query.isEmpty()) {
            throw new IllegalArgumentException("empty query");
        }

        HttpRequest request = createRequest("search", params);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (response.statusCode() != 200) {
                log.fatal("Interweb request failed; Error code: {}; params: {}; response: {}", response.statusCode(), params, responseBody);
                throw new RuntimeException("Interweb request failed, response: " + responseBody);
            }

            return new Gson().fromJson(responseBody, SearchResponse.class);
        } catch (IOException | InterruptedException e) {
            log.fatal("An error occurred during Interweb request {}", request, e);
            return null;
        }
    }
}
