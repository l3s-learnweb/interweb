package de.l3s.interweb.connector.ollama;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.connector.ollama.entity.TagsResponse;
import de.l3s.interweb.connector.ollama.entity.CompletionBody;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;
import de.l3s.interweb.core.completion.UsagePrice;
import de.l3s.interweb.core.completion.Usage;
import de.l3s.interweb.core.completion.Choice;

import org.jboss.logging.Logger;

@Dependent
public class OllamaConnector implements CompletionConnector {
    private static final Logger log = Logger.getLogger(OllamaConnector.class);

    private static final Map<String, UsagePrice> models = new HashMap<>();

    @Override
    public String getName() {
        return "Ollama";
    }

    @Override
    public String getBaseUrl() {
        return "https://ollama.com/";
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
    OllamaClient ollama;

    @Override
    public Uni<CompletionResults> complete(CompletionQuery query) throws ConnectorException {
        return ollama.chatCompletions(new CompletionBody(query)).map(response -> {
            Usage usage = new Usage(
                response.getPromptEvalCount(),
                response.getEvalCount()
            );

            List<Choice> choices = List.of(
                new Choice(
                    0,
                    response.getDoneReason(),
                    new Message(
                        Message.Role.assistant,
                        response.getMessage().getContent()
                    )
                )
            );

            CompletionResults results = new CompletionResults();
            results.setModel(response.getModel());
            results.setUsage(usage);
            results.setChoices(choices);
            results.setCreated(Instant.now());
            return results;
        });
    }

    @Override
    public boolean validate() {
        TagsResponse tags;
        try {
            tags = ollama.tags().await().indefinitely();
        } catch (Exception e) {
            log.error("Failed to validate Ollama connector", e);
            return false;
        }

        List<String> models = tags.getModels().stream().map(model -> model.getName()).toList();
        if (models.isEmpty()) {
            log.warn("No models found in Ollama connector");
            return false;
        }
        
        OllamaConnector.models.clear();
        for (String model : models) {
            OllamaConnector.models.put(model, new UsagePrice(0, 0));
        }

        return true;
    }
}
