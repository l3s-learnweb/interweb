package de.l3s.interweb.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Conversation;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
import de.l3s.interweb.core.search.SearchQuery;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.core.suggest.SuggestQuery;
import de.l3s.interweb.core.suggest.SuggestResults;
import de.l3s.interweb.core.util.StringUtils;

public class Interweb implements Serializable {
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
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

    public SearchResults search(SearchQuery query) throws InterwebException {
        return sendPostRequest("/search", query, SearchResults.class);
    }

    public SuggestResults suggest(SuggestQuery query) throws InterwebException {
        return sendPostRequest("/suggest", query, SuggestResults.class);
    }

    public SuggestResults suggest(String query, String language) throws InterwebException {
        final SuggestQuery params = new SuggestQuery();
        params.setQuery(query);
        params.setLanguage(language);

        return sendPostRequest("/suggest", params, SuggestResults.class);
    }

    public DescribeResults describe(DescribeQuery query) throws InterwebException {
        return sendPostRequest("/describe", query, DescribeResults.class);
    }

    public DescribeResults describe(String link) throws InterwebException {
        final DescribeQuery params = new DescribeQuery();
        params.setLink(link);

        return sendPostRequest("/describe", params, DescribeResults.class);
    }

    public CompletionResults completions(CompletionQuery query) throws InterwebException {
        return sendPostRequest("/chat/completions", query, CompletionResults.class);
    }

    public List<Conversation> chatAll(String user) throws InterwebException {
        return sendGetRequest("/chat", Map.of("user", user), new TypeReference<>() {});
    }

    public Conversation chatById(String uuid) throws InterwebException {
        return sendGetRequest("/chat/" + uuid, null, new TypeReference<>() {});
    }

    public void chatComplete(Conversation conversation) throws InterwebException {
        CompletionResults results = sendPostRequest("/chat/completions", conversation, CompletionResults.class);
        if (results.getLastMessage() != null) {
            conversation.addMessage(results.getLastMessage());
        }
        if (results.getChatTitle() != null) {
            conversation.setTitle(results.getChatTitle());
        }
        if (results.getCost() != null) {
            conversation.setEstimatedCost(results.getCost().getChat());
        }
        if (results.getUsage() != null) {
            conversation.setUsedTokens(results.getUsage().getTotalTokens());
        }
    }

    private URI createRequestUri(final String apiPath, final Map<String, String> params) {
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

    public <T> T sendGetRequest(final String apiPath, final Map<String, String> params, TypeReference<T> valueType) throws InterwebException {
        try {
            final URI uri = createRequestUri(apiPath, params);
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri).GET();

            HttpResponse<String> response = sendRequest(builder);
            return mapper.readValue(response.body(), valueType);
        } catch (IOException e) {
            throw new InterwebException("An error occurred during Interweb request " + apiPath, e);
        }
    }

    public <T> T sendPostRequest(final String apiPath, final Object query, Class<T> valueType) throws InterwebException {
        try {
            String body = mapper.writeValueAsString(query);

            final URI uri = createRequestUri(apiPath, null);
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
            builder.POST(HttpRequest.BodyPublishers.ofString(body)).header("Content-Type", "application/json");

            HttpResponse<String> response = sendRequest(builder);
            return mapper.readValue(response.body(), valueType);
        } catch (IOException e) {
            throw new InterwebException("An error occurred during Interweb request " + query, e);
        }
    }

    public HttpResponse<String> sendRequest(final HttpRequest.Builder builder) throws InterwebException {
        try {
            builder.header("Api-Key", apikey);
            builder.header("Accept", "application/json");

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new InterwebException("Interweb request failed, response: " + response.body());
            }

            return response;
        } catch (IOException | InterruptedException e) {
            throw new InterwebException("An error occurred during Interweb request", e);
        }
    }
}
