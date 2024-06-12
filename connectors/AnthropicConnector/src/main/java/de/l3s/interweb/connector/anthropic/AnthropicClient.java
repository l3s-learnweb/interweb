package de.l3s.interweb.connector.anthropic;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.anthropic.entity.CompletionResponse;
import de.l3s.interweb.connector.anthropic.entity.CompletionBody;
import de.l3s.interweb.core.ConnectorException;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "anthropic")
@ClientHeaderParam(name = "x-api-key", value = "${connector.anthropic.apikey}")
@ClientHeaderParam(name = "anthropic-version", value = "2023-06-01")
public interface AnthropicClient {

    /**
     * Anthropic Completion API
     * https://docs.anthropic.com/en/api/messages
     */
    @POST
    @Path("/v1/messages")
    Uni<CompletionResponse> chatCompletions(CompletionBody body);

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
