package de.l3s.interweb.client;

import java.io.IOException;
import java.io.Serial;
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

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.chat.Conversation;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
import de.l3s.interweb.core.models.ModelsResults;
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

    public ModelsResults models() throws InterwebException {
        return sendGetRequest("/models", Map.of(), new TypeReference<>() {});
    }

    public CompletionsResults chatCompletions(CompletionsQuery query) throws InterwebException {
        return sendPostRequest("/chat/completions", query, CompletionsResults.class);
    }

    public List<Conversation> chatAll() throws InterwebException {
        return sendGetRequest("/chats", Map.of(), new TypeReference<>() {});
    }

    public List<Conversation> chatAll(String user) throws InterwebException {
        return sendGetRequest("/chats", Map.of("user", user), new TypeReference<>() {});
    }

    public Conversation chatById(String uuid) throws InterwebException {
        return sendGetRequest("/chats/" + uuid, null, new TypeReference<>() {});
    }

    public void chatComplete(Conversation conversation) throws InterwebException {
        CompletionsResults results = sendPostRequest("/chat/completions", conversation, CompletionsResults.class);
        if (conversation.getId() == null) {
            conversation.setId(results.getChatId());
        }
        if (conversation.getTitle() == null && results.getChatTitle() != null) {
            conversation.setTitle(results.getChatTitle());
        }
        if (results.getCost() != null) {
            conversation.setEstimatedCost(results.getCost().getChatTotal());
        }
        if (results.getUsage() != null) {
            conversation.setUsedTokens(results.getUsage().getTotalTokens());
        }
        if (conversation.getCreated() == null && results.getCreated() != null) {
            conversation.setCreated(results.getCreated());
        }
        if (results.getLastMessage() != null) {
            conversation.addMessage(results.getLastMessage());
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

    private <T> T sendGetRequest(final String apiPath, final Map<String, String> params, TypeReference<T> valueType) throws InterwebException {
        try {
            final URI uri = createRequestUri(apiPath, params);
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri).GET();

            HttpResponse<String> response = sendRequest(builder);
            return mapper.readValue(response.body(), valueType);
        } catch (IOException e) {
            throw new InterwebException("Failed GET request " + apiPath, e);
        }
    }

    private <T> T sendPostRequest(final String apiPath, final Object query, Class<T> valueType) throws InterwebException {
        try {
            String body = mapper.writeValueAsString(query);

            final URI uri = createRequestUri(apiPath, null);
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
            builder.POST(HttpRequest.BodyPublishers.ofString(body)).header("Content-Type", "application/json");

            HttpResponse<String> response = sendRequest(builder);
            return mapper.readValue(response.body(), valueType);
        } catch (IOException e) {
            throw new InterwebException("Failed POST request " + query, e);
        }
    }

    private HttpResponse<String> sendRequest(final HttpRequest.Builder builder) throws InterwebException {
        try {
            builder.header("Authorization", "Bearer " + apikey);
            builder.header("Accept", "application/json");

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new InterwebException("Bad response: " + response.statusCode() + ", " + response.body());
            }

            return response;
        } catch (IOException e) {
            throw new InterwebException("An error occurred during Interweb request", e);
        } catch (InterruptedException e) {
            java.lang.Thread.currentThread().interrupt();
            throw new InterwebException("An Interweb request was interrupted", e);
        }
    }
}
