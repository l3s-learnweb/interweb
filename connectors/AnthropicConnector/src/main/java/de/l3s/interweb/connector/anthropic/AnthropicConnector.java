package de.l3s.interweb.connector.anthropic;

import java.time.Instant;
import java.util.Map;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.connector.anthropic.entity.AnthropicContent;
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

@Dependent
public class AnthropicConnector implements CompletionConnector {

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
            
            
            AnthropicContent content = response.getContent().get(0);
            Message message = new Message(Message.Role.user, content.getText());
            Choice choice = new Choice(0, response.getStopReason(), message);
            

            CompletionResults results = new CompletionResults(
                query.getModel(),
                usage,
                choice,
                Instant.now()
            );
                
            return results;
        });
    }
}
