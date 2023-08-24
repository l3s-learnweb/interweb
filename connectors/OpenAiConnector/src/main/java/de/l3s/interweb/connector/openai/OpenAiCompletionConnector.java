package de.l3s.interweb.connector.openai;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.Dependent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.*;

@Dependent
public class OpenAiCompletionConnector implements CompletionConnector {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String version = "2023-05-15";

    @Override
    public String getName() {
        return "OpenAI";
    }

    @Override
    public String getBaseUrl() {
        return "https://openai.com/";
    }

    @Override
    public String[] getModels() {
        return new String[]{"gpt-35-turbo", "gpt-35-turbo-16k", "gpt-4"};
    }

    @Override
    public CompletionResults complete(CompletionQuery query, AuthCredentials credentials) throws ConnectorException {
        HttpRequest httpRequest = createRequest(query, credentials);
        CompletionResults response = sendRequest(httpRequest);

        if (response.getChoices().isEmpty()) {
            throw new ConnectorException("No results found");
        }

        return response;
    }

    private HttpRequest createRequest(final CompletionQuery query, AuthCredentials credentials) throws ConnectorException {
        try {
            URI requestUri = URI.create("https://" + credentials.getKey() + "/openai/deployments/" + query.getModel() + "/chat/completions?api-version=" + version);
            String requestBody = mapper.writeValueAsString(new CompletionBody(query));

            return HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .uri(requestUri)
                    .header("Content-Type", "application/json")
                    .header("api-key", credentials.getSecret())
                    .build();
        } catch (JsonProcessingException e) {
            throw new ConnectorException("Unable to serialize request", e);
        }
    }

    private CompletionResults sendRequest(final HttpRequest request) throws ConnectorException {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                CompletionResponse parsedResponse = mapper.readValue(response.body(), CompletionResponse.class);
                return parsedResponse.toCompletionResults();
            }

            ErrorResponse errorResponse = mapper.readValue(response.body(), ErrorResponse.class);
            throw new ConnectorException("OpenAI request failed: " + errorResponse.error.message);
        } catch (IOException | InterruptedException e) {
            throw new ConnectorException("A network error occurred during OpenAI request.", e);
        }
    }

    private record CompletionBody(Double temperature, Double topP, Double frequencyPenalty, Double presencePenalty, Integer maxTokens, List<Message> messages) {
        CompletionBody(CompletionQuery query) {
            this(query.getTemperature(), query.getTopP(), query.getFrequencyPenalty(), query.getPresencePenalty(), query.getMaxTokens(), query.getMessages());
        }
    }

    private record CompletionResponse(String id, String object, Long created, String model, ArrayList<Choice> choices, Usage usage) {
        CompletionResults toCompletionResults() {
            CompletionResults results = new CompletionResults();
            results.add(choices);
            results.setModel(model);
            results.setUsage(usage);
            results.setCreated(Instant.ofEpochSecond(created));
            return results;
        }
    }

    private record ErrorResponse(int statusCode, Error error) {
    }

    private record Error(String message, String type, String param, String code) {
    }
}
