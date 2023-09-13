package de.l3s.interweb.connector.google;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.core.ConnectorException;

@Path("/complete")
@RegisterRestClient(configKey = "google-suggest", baseUri = "https://google.com")
public interface GoogleSuggestClient {

    @GET
    @Path("/search")
    @ClientQueryParam(name = "client", value = "firefox")  // for firefox it returns more results (10 instead of 8 for chrome)
    Uni<String> search(@QueryParam("q") String query, @QueryParam("hl") String lang);

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
