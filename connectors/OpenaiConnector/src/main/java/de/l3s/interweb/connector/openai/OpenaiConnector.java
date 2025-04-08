package de.l3s.interweb.connector.openai;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.openai.entity.CompletionsBody;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.ChatConnector;
import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.UsagePrice;

@Dependent
public class OpenaiConnector implements ChatConnector {
    private static final Logger log = Logger.getLogger(OpenaiConnector.class);

    /**
     * UK South, US-Dollar prices (as EUR price is automatically converted from USD it's floating a bit)
     * https://azure.microsoft.com/de-de/pricing/details/cognitive-services/openai-service/
     */
    private static final List<Model> models = List.of(
        Model.of("gpt-35-turbo", "openai", new UsagePrice(0.5, 1.5), LocalDate.of(2024, 1, 25)),
        Model.of("gpt-4o", "openai", new UsagePrice(2.5, 10), LocalDate.of(2024, 11,20)),
        Model.of("gpt-4o-mini", "openai", new UsagePrice(0.15, 0.60), LocalDate.of(2024, 7, 18)),
        Model.of("o1", "openai", new UsagePrice(15, 60), LocalDate.of(2024, 12, 17)),
        Model.of("o3-mini", "openai", new UsagePrice(1.1, 4.4), LocalDate.of(2025, 1, 31))
    );

    @RestClient
    OpenaiClient openai;

    @Override
    public String getName() {
        return "OpenAI";
    }

    @Override
    public String getBaseUrl() {
        return "https://openai.com/";
    }

    @Override
    public Uni<List<Model>> getModels() {
        return Uni.createFrom().item(models);
    }

    @Override
    public Uni<CompletionsResults> completions(CompletionsQuery query) throws ConnectorException {
        return openai.chatCompletions(query.getModel(), new CompletionsBody(query)).map(response -> {
            CompletionsResults results = response.toCompletionResults();
            results.setModel(query.getModel());
            return results;
        });
    }

    @Override
    public boolean validate() {
        Optional<String> apikey = ConfigProvider.getConfig().getOptionalValue("connector.openai.apikey", String.class);
        if (apikey.isEmpty() || apikey.get().length() < 32) {
            log.warn("API key is empty, please provide a valid API key in the configuration.");
            return false;
        }
        return true;
    }
}
