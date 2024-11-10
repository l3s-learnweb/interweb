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
import io.vertx.core.eventbus.EventBus;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import de.l3s.interweb.core.ObjectWrapper;
import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.models.Model;
import de.l3s.interweb.server.Roles;
import de.l3s.interweb.server.features.api.ApiChatRequest;
import de.l3s.interweb.server.features.api.ApiKey;
import de.l3s.interweb.server.features.chat.ChatService;
import de.l3s.interweb.server.features.models.ModelsService;

@Tag(name = "OpenAI Compatible API v1")
@Path("/v1")
@RolesAllowed({Roles.APPLICATION})
public class OpenaiV1Resource {

    @Inject
    EventBus bus;

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @Inject
    ModelsService modelsService;

    @POST
    @Path("/completions")
    public Uni<CompletionsResults> completions() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @POST
    @Path("/chat/completions")
    public Uni<CompletionsResults> chatCompletions(@Valid CompletionsQuery query) {
        ApiKey apikey = securityIdentity.getCredential(ApiKey.class);
        return chatService.completions(query).chain(results -> {
            results.setChatId(null); // reset chatId if it was set
            bus.send("api-request-chat", ApiChatRequest.of(results, apikey));
            return Uni.createFrom().item(results);
        });
    }

    @GET
    @Path("/models")
    public Uni<ObjectWrapper<List<Model>>> models() {
        return modelsService.getModels().map(models -> new ObjectWrapper<>("list", models));
    }

    @GET
    @Path("/models/{model}")
    public Uni<Model> chat(@PathParam("model") String model) {
        return modelsService.getModel(model);
    }

    @POST
    @Path("/embeddings")
    public Uni<CompletionsResults> embeddings() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
