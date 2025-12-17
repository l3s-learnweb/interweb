package de.l3s.interweb.connector.openai;

import java.io.StringReader;

import de.l3s.interweb.core.chat.CompletionsQuery;

import de.l3s.interweb.core.chat.CompletionsResults;

import de.l3s.interweb.core.embeddings.EmbeddingsQuery;

import de.l3s.interweb.core.embeddings.EmbeddingsResults;

import de.l3s.interweb.core.models.ModelsResults;

import io.smallrye.mutiny.Multi;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.core.ConnectorException;

import org.jboss.resteasy.reactive.common.util.RestMediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "openai")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${connector.openai.apikey}")
@ClientHeaderParam(name = "OpenAI-Organization", value = "${connector.openai.organization}", required = false)
@ClientHeaderParam(name = "OpenAI-Project", value = "${connector.openai.project}", required = false)
public interface OpenaiClient {

    /**
     * OpenAI Models API
     * Note: It gives a list of all possible models, not just the ones we have deployed
     */
    @GET
    @Path("/models")
    Uni<ModelsResults> models();

    /**
     * OpenAI Embeddings API
     * https://learn.microsoft.com/en-us/azure/ai-foundry/openai/latest?view=foundry-classic#create-embedding
     * https://platform.openai.com/docs/api-reference/embeddings/create
     */
    @POST
    @Path("/embeddings")
    Uni<EmbeddingsResults> embeddings(EmbeddingsQuery body);

    /**
     * OpenAI Completion API
     * https://learn.microsoft.com/en-us/azure/ai-foundry/openai/latest?view=foundry-classic#create-chat-completion
     * https://platform.openai.com/docs/api-reference/chat/create
     */
    @POST
    @Path("/chat/completions")
    Uni<CompletionsResults> chatCompletions(CompletionsQuery body);

    @POST
    @Path("/chat/completions")
    @Produces(RestMediaType.APPLICATION_NDJSON)
    Multi<CompletionsResults> chatCompletionsStream(CompletionsQuery body);

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
                    JsonObject errorObj = jsonObject.getJsonObject("error");
                    if (errorObj.containsKey("message")) {
                        return new ConnectorException(errorObj.getString("message"));
                    }
                    if (errorObj.containsKey("code")) {
                        String code = errorObj.getString("code");
                        return new ConnectorException("OpenAI API Error " + code + " (HTTP " + statusCode + ")", responseBody);
                    }
                }

                if (jsonObject.containsKey("message")) {
                    return new ConnectorException(jsonObject.getString("message"));
                }
            }
        } catch (Exception e) {
            // If JSON parsing fails, fall back to generic message
        }

        return new ConnectorException("OpenAI API responded with HTTP " + statusCode, responseBody);
    }
}
