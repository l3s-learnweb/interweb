package de.l3s.interweb.connector.openai;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.l3s.interweb.core.embeddings.EmbeddingConnector;

import de.l3s.interweb.core.embeddings.EmbeddingsQuery;
import de.l3s.interweb.core.embeddings.EmbeddingsResults;

import de.l3s.interweb.core.models.ModelsResults;

import io.smallrye.mutiny.Multi;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.ChatConnector;
import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.UsagePrice;

@Dependent
public class OpenaiConnector implements ChatConnector, EmbeddingConnector {
    private static final Logger log = Logger.getLogger(OpenaiConnector.class);

    /**
     * Schweden Central, US-Dollar prices (as EUR price is automatically converted from USD it's floating a bit)
     * https://azure.microsoft.com/de-de/pricing/details/cognitive-services/openai-service/
     * alternative source https://platform.openai.com/docs/pricing (usually matches)
     */
    private static final List<Model> models = List.of(
        // embeddings
        Model.of("text-embedding-3-small", "openai", new UsagePrice(0.02, 0), LocalDate.of(2024, 1, 25)),
        Model.of("text-embedding-3-large", "openai", new UsagePrice(0.13, 0), LocalDate.of(2024, 1, 25)),
        // text
        Model.of("gpt-4o-mini", "openai", new UsagePrice(0.15, 0.60), LocalDate.of(2024, 7, 18)),
        Model.of("gpt-4o-mini", "openai", new UsagePrice(0.15, 0.60), LocalDate.of(2024, 7, 18)),
        Model.of("gpt-4o", "openai", new UsagePrice(2.5, 10), LocalDate.of(2024, 11,20)),
        Model.of("o1", "openai", new UsagePrice(15, 60), LocalDate.of(2024, 12, 17)),
        Model.of("gpt-4.1", "openai", new UsagePrice(2, 8), LocalDate.of(2025, 4, 14)),
        Model.of("gpt-4.1-mini", "openai", new UsagePrice(0.4, 1.6), LocalDate.of(2025, 4, 14)),
        Model.of("gpt-4.1-nano", "openai", new UsagePrice(0.1, 0.4), LocalDate.of(2025, 4, 14)),
        Model.of("o4-mini", "openai", new UsagePrice(1.1, 4.4), LocalDate.of(2025, 4, 16)),
        Model.of("gpt-5-mini", "openai", new UsagePrice(0.25, 2), LocalDate.of(2025, 8, 7)),
        Model.of("gpt-5-nano", "openai", new UsagePrice(0.05, 0.4), LocalDate.of(2025, 8, 7)),
        Model.of("gpt-5", "openai", new UsagePrice(1.25, 10), LocalDate.of(2025, 10, 3)),
        Model.of("gpt-5.1", "openai", new UsagePrice(1.25, 10), LocalDate.of(2025, 11, 13))
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
    public Uni<ModelsResults> models() {
        return Uni.createFrom().item(new ModelsResults(models));
    }

    @Override
    public Uni<EmbeddingsResults> embeddings(EmbeddingsQuery query) throws ConnectorException {
        return openai.embeddings(query);
    }

    @Override
    public Uni<CompletionsResults> completions(CompletionsQuery query) throws ConnectorException {
        return openai.chatCompletions(query);
    }

    @Override
    public Multi<CompletionsResults> completionsStream(CompletionsQuery query) throws ConnectorException {
        query.setStream(true);
        return openai.chatCompletionsStream(query);
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
