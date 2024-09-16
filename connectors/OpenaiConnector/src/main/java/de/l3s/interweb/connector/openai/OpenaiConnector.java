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
        Model.of("gpt-35-turbo", "openai", new UsagePrice(0.002, 0.002), LocalDate.of(2023, 3, 1)),
        Model.of("gpt-35-turbo-16k", "openai", new UsagePrice(0.003, 0.004), LocalDate.of(2023, 6, 13)),
        Model.of("gpt-35-turbo-1106", "openai", new UsagePrice(0.001, 0.002), LocalDate.of(2023, 11, 6)),
        Model.of("gpt-4", "openai", new UsagePrice(0.03, 0.06), LocalDate.of(2023, 6, 13)),
        Model.of("gpt-4-turbo", "openai", new UsagePrice(0.01, 0.03), LocalDate.of(2024, 1, 25)),
        Model.of("gpt-4o", "openai", new UsagePrice(0.005, 0.015), LocalDate.of(2024, 5, 13))
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
            CompletionsResults results = new CompletionsResults();
            results.setModel(query.getModel());
            results.setCreated(response.getCreated());
            results.setChoices(response.getChoices());
            results.setUsage(response.getUsage());
            results.setObject(response.getObject());
            results.setSystemFingerprint(response.getSystemFingerprint());
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
