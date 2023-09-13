package de.l3s.interweb.connector.vimeo;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.vimeo.entity.VimeoResponse;
import de.l3s.interweb.core.ConnectorException;

@Produces("application/vnd.vimeo.*+json; version=3.2")
@RegisterRestClient(configKey = "vimeo")
@ClientHeaderParam(name = "Authorization", value = "bearer ${connector.vimeo.apikey}")
public interface VimeoSearchClient {

    /**
     * Vimeo Search API
     * https://developer.vimeo.com/api/reference/videos#search_videos
     *
     * @param query The search query.
     * @param page The page number of the results to show.
     * @param perPage The number of items to show on each page of results, up to a maximum of 100.
     * @param sort The way to sort the results.
     *             alphabetical - Sort the results alphabetically.
     *             comments - Sort the results by number of comments.
     *             date - Sort the results by date.
     *             duration - Sort the results by duration.
     *             likes - Sort the results by number of likes.
     *             plays - Sort the results by number of plays.
     *             relevant - Sort the results by relevance.
     * @param direction The sort direction of the results:
     *                  asc - Sort the results in ascending order.
     *                  desc - Sort the results in descending order.
     */
    @GET
    @Path("/videos")
    Uni<VimeoResponse> search(
            @NotNull @QueryParam("query") String query,
            @QueryParam("page") Integer page,
            @QueryParam("per_page") Integer perPage,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction
    );

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
