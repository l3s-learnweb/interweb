package de.l3s.interweb.connector.openai;

import java.util.Map;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.*;

@Dependent
public class OpenaiConnector implements CompletionConnector {

    private static final String version = "2023-05-15";
    private static final Map<String, UsagePrice> models = Map.of(
            "gpt-35-turbo", new UsagePrice(0.0014, 0.0019),
            "gpt-35-turbo-16k", new UsagePrice(0.003, 0.004),
            "gpt-4", new UsagePrice(0.028, 0.056),
            "gpt-4-32k", new UsagePrice(0.056, 0.111)
    );

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
        return models.keySet().toArray(new String[0]);
    }

    @Override
    public UsagePrice getPrice(String model) {
        return models.get(model);
    }

    @RestClient
    OpenaiClient openai;

    @Override
    public Uni<CompletionResults> complete(CompletionQuery query, AuthCredentials credentials) throws ConnectorException {
        return openai.chatCompletions(query.getModel(), version, query);
    }
}
