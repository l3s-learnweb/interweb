package de.l3s.interweb.server.features.openai;

import de.l3s.interweb.core.models.ModelsResults;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.embeddings.EmbeddingsQuery;
import de.l3s.interweb.core.embeddings.EmbeddingsResults;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.server.Roles;
import de.l3s.interweb.server.features.api.ApiKey;
import de.l3s.interweb.server.features.chat.ChatService;
import de.l3s.interweb.server.features.chat.EmbeddingService;
import de.l3s.interweb.server.features.models.ModelsResource;

@Tag(name = "OpenAI Compatible API v1")
@Path("/v1")
@RolesAllowed({Roles.APPLICATION})
public class OpenaiV1Resource {

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @Inject
    ModelsResource modelsResource;

    @Inject
    EmbeddingService embeddingService;

    @POST
    @Path("/completions")
    public Uni<CompletionsResults> completions() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @POST
    @Path("/chat/completions")
    public Uni<CompletionsResults> chatCompletions(@Valid CompletionsQuery query) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);

        return chatService.completions(query, apikey).chain(results -> {
            return populateMoreChoices(apikey, query, results);
        });
    }

    public Uni<CompletionsResults> populateMoreChoices(ApiKey apikey, CompletionsQuery query, CompletionsResults results) {
        if (query.getN() == null || query.getN() <= 1 || results.getChoices().size() >= query.getN()) {
            return Uni.createFrom().item(results);
        }

        return chatService.completions(query, apikey).chain(newResults -> {
            results.getChoices().addAll(newResults.getChoices());
            results.getCost().add(newResults.getCost());
            results.getUsage().add(newResults.getUsage());
            results.getDuration().add(newResults.getDuration());
            return populateMoreChoices(apikey, query, results);
        });
    }

    @GET
    @Path("/models")
    public Uni<ModelsResults> models() {
        return modelsResource.list();
    }

    @GET
    @Path("/models/{model}")
    public Uni<Model> getModel(@PathParam("model") String model) {
        return modelsResource.get(model);
    }

    @POST
    @Path("/embeddings")
    public Uni<EmbeddingsResults> embeddings(@Valid EmbeddingsQuery query) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);
        return embeddingService.embeddings(query, apikey);
    }
}
