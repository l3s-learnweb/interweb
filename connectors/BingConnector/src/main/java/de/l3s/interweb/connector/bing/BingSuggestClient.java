package de.l3s.interweb.connector.bing;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.core.ConnectorException;

@RegisterRestClient(configKey = "bing-suggest", baseUri = "https://api.bing.com")
public interface BingSuggestClient {

    @GET
    @Path("/osjson.aspx")
    Uni<String> search(@QueryParam("query") String query, @QueryParam("market") String market);

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
