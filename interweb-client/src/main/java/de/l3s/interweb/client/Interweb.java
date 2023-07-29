package de.l3s.interweb.client;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.core.util.StringUtils;

public class Interweb implements Serializable {
    @Serial
    private static final long serialVersionUID = 7231324400348062196L;

    private final ObjectMapper objectMapper;
    private final String serverUrl;
    private final String apikey;

    public Interweb(String serverUrl, String apikey) {
        this.serverUrl = serverUrl;
        this.apikey = apikey;

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private URI createRequestUri(final String apiPath, final TreeMap<String, String> params) {
        StringBuilder sb = new StringBuilder();

        sb.append(serverUrl);
        if (sb.charAt(sb.length() - 1) != '/') {
            sb.append('/');
        }
        sb.append(apiPath);

        boolean isFirst = true;
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(isFirst ? '?' : '&').append(entry.getKey()).append('=').append(StringUtils.percentEncode(entry.getValue()));
            isFirst = false;
        }

        return URI.create(sb.toString());
    }

    private HttpRequest createRequest(final String apiPath, final TreeMap<String, String> params) {
        final URI apiUri = createRequestUri(apiPath, params);

        return HttpRequest.newBuilder().GET()
                .uri(apiUri)
                .header("Accept", "application/json")
                .header("Api-Key", apikey)
                .build();
    }

    public SearchResults search(TreeMap<String, String> params) throws InterwebException {
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
                throw new InterwebException("Interweb request failed, response: " + responseBody);
            }

            return objectMapper.readValue(responseBody, SearchResults.class);
        } catch (IOException | InterruptedException e) {
            throw new InterwebException("An error occurred during Interweb request " + request, e);
        }
    }
}
