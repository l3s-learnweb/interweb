package de.l3s.interweb.connector.openai;

import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.openai.entity.CompletionBody;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.UsagePrice;

@Dependent
public class OpenaiConnector implements CompletionConnector {
    private static final Logger log = Logger.getLogger(OpenaiConnector.class);

    /**
     * UK South, US-Dollar prices (as EUR price is automatically converted from USD it's floating a bit)
     * https://azure.microsoft.com/de-de/pricing/details/cognitive-services/openai-service/
     */
    private static final Map<String, UsagePrice> models = Map.of(
        "gpt-35-turbo", new UsagePrice(0.002, 0.002),
        "gpt-35-turbo-16k", new UsagePrice(0.003, 0.004),
        "gpt-35-turbo-1106", new UsagePrice(0.001, 0.002),
        "gpt-4-turbo", new UsagePrice(0.01, 0.03),
        "gpt-4", new UsagePrice(0.03, 0.06),
        "gpt-4-32k", new UsagePrice(0.06, 0.12)
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
        return openai.chatCompletions(query.getModel(), new CompletionBody(query)).map(response -> {
            CompletionResults results = new CompletionResults();
            results.setModel(query.getModel());
            results.setCreated(response.getCreated());
            results.setChoices(response.getChoices());
            results.setUsage(response.getUsage());
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
