package de.l3s.interweb.connector.anthropic;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.anthropic.entity.AnthropicUsage;
import de.l3s.interweb.connector.anthropic.entity.CompletionBody;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.*;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.UsagePrice;

@Dependent
public class AnthropicConnector implements ChatConnector {
    private static final Logger log = Logger.getLogger(AnthropicConnector.class);

    /**
     * https://www.anthropic.com/pricing#anthropic-api
     * https://docs.anthropic.com/en/docs/about-claude/models
     */
    private static final List<Model> models = List.of(
        Model.of("claude-3-5-sonnet-20240620", "anthropic", new UsagePrice(3, 15), LocalDate.of(2024, 6, 20)),
        Model.of("claude-3-opus-20240229", "anthropic", new UsagePrice(15, 75), LocalDate.of(2024, 2, 29)),
        Model.of("claude-3-sonnet-20240229", "anthropic", new UsagePrice(3, 15), LocalDate.of(2024, 2, 29)),
        Model.of("claude-3-haiku-20240307", "anthropic", new UsagePrice(0.25, 1.25), LocalDate.of(2024, 3, 7)),
        Model.of("claude-2.1", "anthropic", new UsagePrice(8, 24), LocalDate.of(2023, 11, 23)),
        Model.of("claude-2.0", "anthropic", new UsagePrice(8, 24), LocalDate.of(2023, 7, 11)),
        Model.of("claude-instant-1.2", "anthropic", new UsagePrice(0.8, 2.4), LocalDate.of(2023, 8, 9))
    );

    @RestClient
    AnthropicClient anthropic;

    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public String getBaseUrl() {
        return "https://anthropic.com/";
    }

    @Override
    public Uni<List<Model>> getModels() {
        return Uni.createFrom().item(models);
    }

    @Override
    public Uni<CompletionsResults> completions(CompletionsQuery query) throws ConnectorException {
        return anthropic.chatCompletions(new CompletionBody(query)).map(response -> {
            AnthropicUsage anthropicUsage = response.getUsage();
            Usage usage = new Usage(
                anthropicUsage.getInputTokens(),
                anthropicUsage.getOutputTokens()
            );

            AtomicInteger index = new AtomicInteger();
            List<Choice> choices = response.getContent().stream().map(content -> {
                Message message = new Message(Role.assistant, content.getText());
                return new Choice(index.getAndIncrement(), response.getStopReason(), message);
            }).toList();

            CompletionsResults results = new CompletionsResults();
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
