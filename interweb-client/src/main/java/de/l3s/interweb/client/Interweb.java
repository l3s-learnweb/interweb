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
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.core.suggest.SuggestQuery;
import de.l3s.interweb.core.suggest.SuggestResults;
import de.l3s.interweb.core.util.StringUtils;

public class Interweb implements Serializable {
    @Serial
    private static final long serialVersionUID = 7231324400348062196L;

    private final ObjectMapper mapper;
    private final String serverUrl;
    private final String apikey;

    public Interweb(String serverUrl, String apikey) {
        this.serverUrl = serverUrl;
        this.apikey = apikey;

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public SearchResults search(SearchQuery query) throws InterwebException {
        return sendRequest("/search", query, SearchResults.class);
    }

    public SuggestResults suggest(SuggestQuery query) throws InterwebException {
        return sendRequest("/suggest", query, SuggestResults.class);
    }

    public CompletionResults chatCompletions(CompletionQuery query) throws InterwebException {
        return sendRequest("/chat/completions", query, CompletionResults.class);
    }

    private URI createRequestUri(final String apiPath, final TreeMap<String, String> params) {
        StringBuilder sb = new StringBuilder();

        sb.append(serverUrl);
        if (sb.charAt(sb.length() - 1) == '/') {
            sb.setLength(sb.length() - 1);
        }
        sb.append(apiPath);

        if (params != null && !params.isEmpty()) {
            boolean isFirst = true;
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(isFirst ? '?' : '&').append(entry.getKey()).append('=').append(StringUtils.percentEncode(entry.getValue()));
                isFirst = false;
            }
        }

        return URI.create(sb.toString());
    }

    public <T> T sendRequest(final String apiPath, final Object query, Class<T> valueType) throws InterwebException {
        try {
            final URI uri = createRequestUri(apiPath, null);
            String body = mapper.writeValueAsString(query);

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                    .uri(uri)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Api-Key", apikey)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (response.statusCode() != 200) {
                throw new InterwebException("Interweb request failed, response: " + responseBody);
            }

            return mapper.readValue(responseBody, valueType);
        } catch (IOException | InterruptedException e) {
            throw new InterwebException("An error occurred during Interweb request " + query, e);
        }
    }
}
