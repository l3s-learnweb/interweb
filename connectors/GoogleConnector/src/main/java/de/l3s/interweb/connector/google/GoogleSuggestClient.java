package de.l3s.interweb.connector.google;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/complete")
@RegisterRestClient(configKey = "google-suggest", baseUri = "https://google.com")
public interface GoogleSuggestClient {

    @GET
    @Path("/search")
    @ClientQueryParam(name = "client", value = "firefox")  // for firefox it returns more results (10 instead of 8 for chrome)
    Uni<String> search(@QueryParam("q") String query, @QueryParam("hl") String lang);
}
