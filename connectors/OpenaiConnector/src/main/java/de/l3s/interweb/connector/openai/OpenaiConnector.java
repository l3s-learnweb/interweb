package de.l3s.interweb.connector.openai;

import java.util.Map;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.connector.openai.entity.CompletionBody;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.UsagePrice;

@Dependent
public class OpenaiConnector implements CompletionConnector {

    private static final String version = "2023-05-15";
    private static final Map<String, UsagePrice> models = Map.of(
            "gpt-35-turbo", new UsagePrice(0.0014, 0.0019),
            "gpt-35-turbo-16k", new UsagePrice(0.003, 0.004),
            "gpt-35-turbo-1106", new UsagePrice(0.001, 0.002),
            "gpt-4-turbo", new UsagePrice(0.010, 0.028),
            "gpt-4", new UsagePrice(0.028, 0.055),
            "gpt-4-32k", new UsagePrice(0.055, 0.109)
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
    public Uni<CompletionResults> complete(CompletionQuery query) throws ConnectorException {
        return openai.chatCompletions(query.getModel(), version, new CompletionBody(query)).map(response -> {
            CompletionResults results = new CompletionResults();
            results.setModel(query.getModel());
            results.setCreated(response.getCreated());
            results.setChoices(response.getChoices());
            results.setUsage(response.getUsage());
            return results;
        });
    }
}
