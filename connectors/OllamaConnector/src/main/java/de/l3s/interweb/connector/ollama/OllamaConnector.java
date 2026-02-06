package de.l3s.interweb.connector.ollama;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import de.l3s.interweb.connector.ollama.entity.*;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.ChatConnector;
import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.embeddings.EmbeddingConnector;
import de.l3s.interweb.core.embeddings.EmbeddingsQuery;
import de.l3s.interweb.core.embeddings.EmbeddingsResults;
import de.l3s.interweb.core.models.ModelPullStatus;
import de.l3s.interweb.core.models.ModelsResults;

@Dependent
public class OllamaConnector implements ChatConnector, EmbeddingConnector {
    private static final Logger log = Logger.getLogger(OllamaConnector.class);

    @RestClient
    OllamaClient ollama;

    private final Map<String, ModelPullStatus> pullStatuses = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "Ollama";
    }

    @Override
    public String getBaseUrl() {
        return "https://ollama.com/";
    }

    @Override
    public Uni<ModelsResults> models() {
        return ollama.tags()
            .map(response -> response.getModels().stream().map(Tag::toModel).toList())
            .map(ModelsResults::new);
    }

    @Override
    public Uni<CompletionsResults> completions(CompletionsQuery query) throws ConnectorException {
        final ChatBody body = ChatBody.of(query);
        return ollama.chat(body).map(ChatResponse::toCompletionResults);
    }

    @Override
    public Multi<CompletionsResults> completionsStream(CompletionsQuery query) throws ConnectorException {
        query.setStream(true);
        final ChatBody body = ChatBody.of(query);
        return ollama.chatStream(body).map(ChatResponse::toCompletionResults);
    }

    @Override
    public Uni<EmbeddingsResults> embeddings(EmbeddingsQuery query) throws ConnectorException {
        final EmbedBody body = EmbedBody.of(query);
        return ollama.embed(body).map(EmbedResponse::toCompletionResults);
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

    /**
     * Pull a model from Ollama.
     * If the model is already being pulled, returns the current status.
     */
    @Override
    public Uni<ModelPullStatus> pullModel(String modelName) {
        String lowerModelName = modelName.toLowerCase();

        ModelPullStatus existing = pullStatuses.get(lowerModelName);
        if (existing != null) {
            return Uni.createFrom().item(existing);
        }

        ModelPullStatus initialStatus = new ModelPullStatus("initiating");
        pullStatuses.put(lowerModelName, initialStatus);

        PullBody body = new PullBody(modelName);

        ollama.pullStream(body)
            .onItem().invoke(response -> {
                pullStatuses.put(lowerModelName, response.toModelPullStatus());
            })
            .collect().last()
            .onItem().invoke(lastResponse -> {
                log.info("Successfully pulled model: " + modelName);
                pullStatuses.put(lowerModelName, lastResponse.toModelPullStatus());
            })
            .onFailure().invoke(error -> {
                log.error("Failed to pull model: " + modelName, error);
                ModelPullStatus errorStatus = new ModelPullStatus("failed: " + error.getMessage());
                pullStatuses.put(lowerModelName, errorStatus);
            })
            .subscribe().with(
                item -> log.debug("Pull completed for model: " + modelName),
                failure -> log.debug("Pull failed for model: " + modelName)
            );

        return Uni.createFrom().item(initialStatus);
    }
}
