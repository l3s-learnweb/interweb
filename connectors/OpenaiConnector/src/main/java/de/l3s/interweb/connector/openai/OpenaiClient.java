package de.l3s.interweb.connector.openai;

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
@ClientQueryParam(name = "api-version", value = "2025-03-01-preview")
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
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
