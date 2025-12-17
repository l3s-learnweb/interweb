package de.l3s.interweb.connector.ollama;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
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

import java.io.StringReader;

/**
 * Ollama Completion API
 * https://github.com/ollama/ollama/blob/main/docs/api.md
 *
 * https://docs.ollama.com/api/chat
 * https://docs.ollama.com/api/embed
 * https://docs.ollama.com/api/tags
 *
 * OpenAPI Specification
 * https://raw.githubusercontent.com/davidmigloz/langchain_dart/refs/heads/main/packages/ollama_dart/oas/ollama-curated.yaml
 */
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "ollama")
public interface OllamaClient {

    @POST
    @Path("/api/chat")
    Uni<ChatResponse> chat(ChatBody body);

    @POST
    @Path("/api/chat")
    @Produces(RestMediaType.APPLICATION_NDJSON)
    Multi<ChatResponse> chatStream(ChatBody body);

    @POST
    @Path("/api/embed")
    Uni<EmbedResponse> embed(EmbedBody body);

    @GET
    @Path("/api/tags")
    Uni<TagsResponse> tags();

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        String responseBody = response.readEntity(String.class);
        return parseErrorMessage(responseBody, response.getStatus());
    }

    private static ConnectorException parseErrorMessage(String responseBody, int statusCode) {
        try {
            if (responseBody != null && !responseBody.trim().isEmpty()) {
                JsonReader jsonReader = Json.createReader(new StringReader(responseBody));
                JsonObject jsonObject = jsonReader.readObject();

                if (jsonObject.containsKey("error")) {
                    return new ConnectorException(jsonObject.getString("error"));
                }
            }
        } catch (Exception e) {
            // If JSON parsing fails, fall back to generic message
        }

        return new ConnectorException("Ollama responded with HTTP " + statusCode, responseBody);
    }
}
