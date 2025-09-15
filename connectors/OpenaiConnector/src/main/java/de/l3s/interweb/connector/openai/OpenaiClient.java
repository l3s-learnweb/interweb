package de.l3s.interweb.connector.openai;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.openai.entity.CompletionsBody;
import de.l3s.interweb.connector.openai.entity.CompletionsResponse;
import de.l3s.interweb.core.ConnectorException;

@Path("/openai/deployments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "openai")
@ClientHeaderParam(name = "api-key", value = "${connector.openai.apikey}")
@ClientQueryParam(name = "api-version", value = "2025-04-01-preview")
public interface OpenaiClient {

    /**
     * OpenAI Completion API
     * https://learn.microsoft.com/en-us/azure/ai-services/openai/reference
     */
    @POST
    @Path("/{model}/chat/completions")
    Uni<CompletionsResponse> chatCompletions(@PathParam("model") String model, CompletionsBody body);

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
