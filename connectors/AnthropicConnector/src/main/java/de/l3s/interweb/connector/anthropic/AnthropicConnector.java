package de.l3s.interweb.connector.anthropic;

import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.connector.anthropic.entity.AnthropicContent;
import de.l3s.interweb.connector.anthropic.entity.CompletionBody;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;
import de.l3s.interweb.core.completion.UsagePrice;
import de.l3s.interweb.core.completion.Usage;
import de.l3s.interweb.core.completion.Choice;

@Dependent
public class AnthropicConnector implements CompletionConnector {

    private static final String version = "2023-06-01";
    private static final Map<String, UsagePrice> models = Map.of(
        "claude-3-opus-20240229", new UsagePrice(0.015, 0.075),
        "claude-3-sonnet-20240229", new UsagePrice(0.003, 0.015),
        "claude-3-haiku-20240307", new UsagePrice(0.00025, 0.00125)
    );

    @Override
    public String getName() {
        return "Anthropic";
    }

    @Override
    public String getBaseUrl() {
        return "https://api.anthropic.com/";
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
        return anthropic.chatCompletions(version, new CompletionBody(query.getModel(), query)).map(response -> {
            CompletionResults results = new CompletionResults();
            results.setModel(query.getModel());
            results.setCreated(Instant.now());

            List<Choice> choices = new ArrayList<>();
            Choice choice = new Choice();
            AnthropicContent content = response.getContent().get(0);
            Message message = new Message();
            message.setContent(content.getText());
            message.setRole(Message.Role.assistant);
            choice.setMessage(message);
            choice.setIndex(0);
            choice.setFinishReason(response.getStopReason());
            choices.add(choice);
            results.setChoices(choices);

            Usage usage = new Usage();
            usage.setPromptTokens(response.getUsage().getInputTokens());
            usage.setCompletionTokens(response.getUsage().getOutputTokens());
            usage.setTotalTokens(response.getUsage().getInputTokens() + response.getUsage().getOutputTokens());
            results.setUsage(usage);
            return results;
        });
    }
}
