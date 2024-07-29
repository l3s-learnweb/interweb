package de.l3s.interweb.connector.giphy;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.giphy.entity.SearchResponse;
import de.l3s.interweb.core.ConnectorException;

@Path("/v1/gifs")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "giphy")
@ClientQueryParam(name = "api_key", value = "${connector.giphy.apikey}")
public interface GiphySearchClient {

    /**
     * Search Endpoint
     * https://developers.giphy.com/docs/api/endpoint/#search
     *
     * @param query Search query term or phrase. Adding @<username> anywhere in the q parameter effectively changes the search query to be a search
     *              for a specific user’s GIFs (user has to be public and verified user by GIPHY.)
     *              If the q parameter contains one of these words: sticker, stickers, or transparent, the search will return stickers content.
     *              Maximum length: 50 chars.
     * @param limit The maximum number of objects to return. (Default: “25”). For beta keys max limit is 50.
     * @param offset Specifies the starting position of the results. Default: “0”. Maximum: “4999”.
     * @param lang Specify default language for regional content; use a 2-letter ISO 639-1 language code.
     *             https://developers.giphy.com/docs/optional-settings/#language-support
     */
    @GET
    @Path("/search")
    @ClientQueryParam(name = "rating", value = "g")
    Uni<SearchResponse> search(
        @NotNull @QueryParam("q") String query,
        @QueryParam("limit") Integer limit,
        @QueryParam("offset") Integer offset,
        @QueryParam("lang") String lang
    );

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
