package de.l3s.interweb.connector.ollama;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.ollama.entity.ChatBody;
import de.l3s.interweb.connector.ollama.entity.TagsResponse;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.*;

@Dependent
public class OllamaConnector implements CompletionConnector {
    private static final Logger log = Logger.getLogger(OllamaConnector.class);

    private static final Map<String, UsagePrice> models = new HashMap<>();

    @RestClient
    OllamaClient ollama;

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
        if (models.isEmpty()) {
            try {
                TagsResponse tags = ollama.tags().await().indefinitely();
                if (tags.getModels().isEmpty()) {
                    throw new ConnectorException("No models deployed on Ollama instance");
                }

                tags.getModels().forEach(tag -> {
                    models.put(tag.getName(), new UsagePrice(0, 0));
                });
            } catch (Exception e) {
                throw new ConnectorException("Failed to fetch Ollama models", e);
            }
        }
        return models.keySet().toArray(new String[0]);
    }

    @Override
    public UsagePrice getPrice(String model) {
        if (models.isEmpty()) {
            getModels(); // make sure models are loaded
        }
        return models.get(model);
    }

    @Override
    public Uni<CompletionResults> complete(CompletionQuery query) throws ConnectorException {
        return ollama.chatCompletions(new ChatBody(query)).map(response -> {
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
        Optional<String> apikey = ConfigProvider.getConfig().getOptionalValue("connector.ollama.url", String.class);
        if (apikey.isEmpty()) {
            log.warn("URL is empty, please provide a valid URL in the configuration.");
            return false;
        }
        return true;
    }
}
