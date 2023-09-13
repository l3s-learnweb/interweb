package de.l3s.interweb.connector.youtube;

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

import de.l3s.interweb.connector.youtube.entity.ListResponse;
import de.l3s.interweb.core.ConnectorException;

@Path("/youtube/v3")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "youtube")
@ClientQueryParam(name = "key", value = "${connector.youtube.apikey}")
public interface YouTubeSearchClient {

    /**
     * YouTube Data API Search: list
     * https://developers.google.com/youtube/v3/docs/search/list
     *
     * @param query specifies the query term to search for.
     *              Your request can also use the Boolean NOT (-) and OR (|) operators to exclude videos or to find videos that are associated with one
     *              of several search terms. For example, to search for videos matching either "boating" or "sailing", set the q parameter value
     *              to boating|sailing. Similarly, to search for videos matching either "boating" or "sailing" but not "fishing", set the q parameter
     *              value to boating|sailing -fishing. Note that the pipe character must be URL-escaped when it is sent in your API request.
     * @param channelId indicates that the API response should only contain resources created by the channel.
     * @param publishedAfter requires resources created at or after the specified time.The value is an RFC 3339 formatted value (1970-01-01T00:00:00Z).
     * @param publishedBefore requires resources created before or at the specified time. The value is an RFC 3339 formatted value (1970-01-01T00:00:00Z).
     * @param maxResults maximum number of items that should be returned in the result set. Acceptable values are 0 to 50, inclusive. The default value is 5.
     * @param lang the parameter value is typically an ISO 639-1 two-letter language code.
     *             Please note that results in other languages will still be returned if they are highly relevant to the search query term.
     * @param order The default value is relevance. Acceptable values are:
     *              date – Resources are sorted in reverse chronological order based on the date they were created.
     *              rating – Resources are sorted from highest to lowest rating.
     *              relevance – Resources are sorted based on their relevance to the search query. This is the default value for this parameter.
     *              title – Resources are sorted alphabetically by title.
     *              viewCount – Resources sorted from highest to lowest number of views.
     * @param pageToken identifies a specific page that should be returned. Use values of nextPageToken and prevPageToken properties in the retrieved response.
     */
    @GET
    @Path("/search")
    @ClientQueryParam(name = "part", value = "id,snippet")
    @ClientQueryParam(name = "safeSearch", value = "moderate")
    @ClientQueryParam(name = "type", value = "video")
    Uni<ListResponse> search(
            @QueryParam("q") String query,
            @QueryParam("channelId") String channelId,
            @QueryParam("publishedAfter") String publishedAfter,
            @QueryParam("publishedBefore") String publishedBefore,
            @QueryParam("maxResults") Integer maxResults,
            @QueryParam("relevanceLanguage") String lang,
            @QueryParam("order") String order,
            @QueryParam("pageToken") String pageToken
    );

    /**
     * YouTube Data API Videos: list
     * https://developers.google.com/youtube/v3/docs/videos/list
     *
     * @param part The part parameter specifies a comma-separated list of one or more video resource properties that the API response will include.
     *             If the parameter identifies a property that contains child properties, the child properties will be included in the response.
     *             For example, in a video resource, the snippet property contains the channelId, title, description, tags, and categoryId properties.
     *             As such, if you set part=snippet, the API response will contain all of those properties.
     *             The following list contains the part names that you can include in the parameter value:
     *              - contentDetails
     *              - fileDetails
     *              - id
     *              - liveStreamingDetails
     *              - localizations
     *              - player
     *              - processingDetails
     *              - recordingDetails
     *              - snippet
     *              - statistics
     *              - status
     *              - suggestions
     *              - topicDetails
     * @param ids The id parameter specifies a comma-separated list of the YouTube video ID(s) for the resource(s) that are being retrieved.
     */
    @GET
    @Path("/videos")
    Uni<ListResponse> videos(
            @QueryParam("part") String part,
            @QueryParam("id") String ids
    );

    /**
     * YouTube Data API Channels: list
     * https://developers.google.com/youtube/v3/docs/channels/list
     *
     * @param username The forUsername parameter specifies a YouTube username, thereby requesting the channel associated with that username.
     */
    @GET
    @Path("/channels")
    @ClientQueryParam(name = "part", value = "id")
    Uni<ListResponse> channels(
            @QueryParam("forUsername") String username
    );

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
