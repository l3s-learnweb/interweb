package de.l3s.interweb.connector.ollama;

import java.util.List;
import java.util.Optional;

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
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.core.models.UsagePrice;

@Dependent
public class OllamaConnector implements ChatConnector, EmbeddingConnector {
    private static final Logger log = Logger.getLogger(OllamaConnector.class);

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
    public Uni<List<Model>> getModels() {
        return ollama.tags().map(tags -> tags.getModels().stream().map(tag -> {
            Model model = new Model();
            model.setId(tag.getName());
            model.setProvidedBy("l3s");
            model.setPrice(UsagePrice.FREE);
            model.setFamily(tag.getDetails().getFamily());
            model.setParameterSize(tag.getDetails().getParameterSize());
            model.setQuantizationLevel(tag.getDetails().getQuantizationLevel());
            model.setCreated(tag.getModifiedAt());
            return model;
        }).toList());
    }

    @Override
    public Uni<CompletionsResults> completions(CompletionsQuery query) throws ConnectorException {
        final ChatBody body = new ChatBody(query);
        return ollama.chat(body).map(ChatResponse::toCompletionResults);
    }

    @Override
    public Multi<CompletionsResults> completionsStream(CompletionsQuery query) throws ConnectorException {
        final ChatStreamBody body = new ChatStreamBody(query);
        return ollama.chatStream(body).map(ChatResponse::toCompletionResults);
    }

    @Override
    public Uni<EmbeddingsResults> embeddings(EmbeddingsQuery query) throws ConnectorException {
        final EmbedBody body = new EmbedBody(query);
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
}
