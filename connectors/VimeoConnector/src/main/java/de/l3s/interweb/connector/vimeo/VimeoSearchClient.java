package de.l3s.interweb.connector.vimeo;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.vimeo.entity.Datum;
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
     */
    @GET
    @Path("/videos")
    Uni<VimeoResponse> search(
        @NotNull @QueryParam("query") String query,
        @QueryParam("page") Integer page,
        @QueryParam("per_page") Integer perPage,
        @QueryParam("sort") String sort
    );

    /**
     * Vimeo Video API. Get a specific video
     * https://developer.vimeo.com/api/reference/videos#get_video
     *
     * @param videoId the ID of the video.
     * @return the video details.
     */
    @GET
    @Path("/videos/{video_id}")
    @ClientQueryParam(name = "fields", value = "link,name,description,duration,player_embed_url,created_time,tags,height,width,stats,pictures,metadata,user")
    Uni<Datum> describe(@NotNull @PathParam("video_id") String videoId);

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        if (response.getStatus() == 404) {
            return new ConnectorException("No results");
        }

        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
