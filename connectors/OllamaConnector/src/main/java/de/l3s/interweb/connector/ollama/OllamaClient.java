package de.l3s.interweb.connector.ollama;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.ollama.entity.ChatBody;
import de.l3s.interweb.connector.ollama.entity.ChatResponse;
import de.l3s.interweb.connector.ollama.entity.TagsResponse;
import de.l3s.interweb.core.ConnectorException;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "ollama")
public interface OllamaClient {

    /**
     * Ollama Completion API
     * https://github.com/ollama/ollama/blob/main/docs/api.md
     */
    @POST
    @Path("/api/chat")
    Uni<ChatResponse> chatCompletions(ChatBody body);

    @GET
    @Path("/api/tags")
    Uni<TagsResponse> tags();

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
