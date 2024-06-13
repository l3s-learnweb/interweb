package de.l3s.interweb.connector.anthropic;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.connector.anthropic.entity.AnthropicUsage;
import de.l3s.interweb.connector.anthropic.entity.CompletionBody;
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
public class AnthropicConnector implements CompletionConnector {
    private static final Logger log = Logger.getLogger(AnthropicConnector.class);

    private static final Map<String, UsagePrice> models = Map.of(
        "claude-3-opus-20240229", new UsagePrice(0.015, 0.075),
        "claude-3-sonnet-20240229", new UsagePrice(0.003, 0.015),
        "claude-3-haiku-20240307", new UsagePrice(0.00025, 0.00125),
         // legacy
        "claude-2.1", new UsagePrice(0.008, 0.024),
        "claude-2.0", new UsagePrice(0.008, 0.024),
        "claude-instant-1.2", new UsagePrice(0.0008, 0.0024)
    );

    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public String getBaseUrl() {
        return "https://anthropic.com/";
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
    AnthropicClient anthropic;

    @Override
    public Uni<CompletionResults> complete(CompletionQuery query) throws ConnectorException {
        return anthropic.chatCompletions(new CompletionBody(query)).map(response -> {
            AnthropicUsage anthropicUsage = response.getUsage();
            Usage usage = new Usage(
                anthropicUsage.getInputTokens(),
                anthropicUsage.getOutputTokens()
            );

            AtomicInteger index = new AtomicInteger();
            List<Choice> choices = response.getContent().stream().map(content -> {
                Message message = new Message(Message.Role.assistant, content.getText());
                return new Choice(index.getAndIncrement(), response.getStopReason(), message);
            }).toList();

            CompletionResults results = new CompletionResults();
            results.setModel(query.getModel());
            results.setUsage(usage);
            results.setChoices(choices);
            results.setCreated(Instant.now());
            return results;
        });
    }

    @Override
    public boolean validate() {
        Optional<String> apikey = ConfigProvider.getConfig().getOptionalValue("connector.anthropic.apikey", String.class);
        if (apikey.isEmpty() || apikey.get().isEmpty()) {
            log.warn("API key is empty, please provide a valid API key in the configuration.");
            return false;
        }
        return true;
    }
}
