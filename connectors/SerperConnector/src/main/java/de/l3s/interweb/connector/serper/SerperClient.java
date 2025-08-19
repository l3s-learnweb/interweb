package de.l3s.interweb.connector.serper;

import de.l3s.interweb.connector.serper.entity.AutocompleteResponse;

import de.l3s.interweb.connector.serper.entity.SearchRequest;

import de.l3s.interweb.connector.serper.entity.SearchResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

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
