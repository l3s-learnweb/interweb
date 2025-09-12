package de.l3s.interweb.connector.google;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.google.serper.AutocompleteResponse;
import de.l3s.interweb.connector.google.serper.SearchRequest;
import de.l3s.interweb.connector.google.serper.SearchResponse;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "serper")
@ClientHeaderParam(name = "X-API-KEY", value = "${connector.serper.apikey}")
@ClientHeaderParam(name = "Content-Type", value = "application/json")
public interface SerperClient {

    @POST
    @Path("/search")
    Uni<SearchResponse> search(SearchRequest requestBody);

    @GET
    @Path("/autocomplete")
    Uni<AutocompleteResponse> autocomplete(@QueryParam("q") String query);
}
