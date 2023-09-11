package de.l3s.interweb.connector.openai;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.util.StringUtils;

@Path("/openai/deployments")
@Consumes("application/json")
@Produces("application/json")
@RegisterRestClient(configKey = "openai")
@ClientHeaderParam(name = "api-key", value = "${connector.openai.apikey}")
public interface OpenaiClient {

    @POST
    @Path("/{model}/chat/completions")
    Uni<CompletionResults> chatCompletions(@PathParam("model") String model, @QueryParam("api-version") String apiVersion, CompletionQuery body);

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        ErrorResponse.Error err = response.readEntity(ErrorResponse.class).error;

        if (StringUtils.isNotEmpty(err.message)) {
            return new ConnectorException(err.message);
        }
        return new ConnectorException("The remote service responded with " + err.status + ": " + err.code);
    }
}
