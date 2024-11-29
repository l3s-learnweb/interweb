package de.l3s.interweb.server.features.openai;

import java.util.List;

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

import de.l3s.interweb.core.ObjectWrapper;
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
            results.setChatId(null); // reset chatId if it was set
            return Uni.createFrom().item(results);
        });
    }

    @GET
    @Path("/models")
    public Uni<ObjectWrapper<List<Model>>> models() {
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
