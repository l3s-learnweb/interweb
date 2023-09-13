package de.l3s.interweb.connector.openai;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;

@Path("/openai/deployments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "openai")
@ClientHeaderParam(name = "api-key", value = "${connector.openai.apikey}")
public interface OpenaiClient {

    @POST
    @Path("/{model}/chat/completions")
    Uni<CompletionResults> chatCompletions(@PathParam("model") String model, @QueryParam("api-version") String apiVersion, CompletionQuery body);

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
