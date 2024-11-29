package de.l3s.interweb.connector.ollama;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.common.util.RestMediaType;

import de.l3s.interweb.connector.ollama.entity.*;
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
    Uni<ChatResponse> chat(ChatBody body);

    @POST
    @Path("/api/chat")
    @Produces(RestMediaType.APPLICATION_NDJSON)
    Multi<ChatResponse> chatStream(ChatStreamBody body);

    @POST
    @Path("/api/embed")
    Uni<EmbedResponse> embed(EmbedBody body);

    @GET
    @Path("/api/tags")
    Uni<TagsResponse> tags();

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
