package de.l3s.interweb.connector.bing;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "bing-suggest", baseUri = "https://api.bing.com")
public interface BingSuggestClient {

    @GET
    @Path("/osjson.aspx")
    Uni<String> search(@QueryParam("query") String query, @QueryParam("market") String market);
}
