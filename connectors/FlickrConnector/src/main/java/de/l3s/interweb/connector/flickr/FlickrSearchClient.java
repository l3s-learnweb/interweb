package de.l3s.interweb.connector.flickr;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.flickr.entity.FlickrResponse;
import de.l3s.interweb.core.ConnectorException;

/**
 * Flickr Services API
 * https://www.flickr.com/services/api/misc.overview.html
 */
@Path("/services/rest/")
@Consumes("application/json")
@Produces("application/json")
@RegisterRestClient(configKey = "flickr")
@ClientQueryParam(name = "api_key", value = "${connector.flickr.apikey}")
@ClientQueryParam(name = "format", value = "json")
@ClientQueryParam(name = "nojsoncallback", value = "1")
public interface FlickrSearchClient {

    /**
     * Flickr Photos Search API
     * https://www.flickr.com/services/api/flickr.photos.search.html
     *
     * @param query A free text search. Photos who's title, description or tags contain the text will be returned.
     *              You can exclude results that match a term by prepending it with a - character.
     * @param media Filter results by media type. Possible values are `all` (default), `photos` or `videos`.
     * @param dateFrom Photos with an upload date greater than or equal to this value will be returned. The date can be in the form of a unix timestamp.
     * @param dateTill Photos with an upload date less than or equal to this value will be returned. The date can be in the form of a unix timestamp.
     * @param sort The order in which to sort returned photos. Defaults to date-posted-desc. The possible values are:
     *             date-posted-asc, date-posted-desc, date-taken-asc, date-taken-desc, interestingness-desc, interestingness-asc, and relevance.
     * @param page The page of results to return. If this argument is omitted, it defaults to 1.
     * @param perPage Number of photos to return per page. If this argument is omitted, it defaults to 100. The maximum allowed value is 500.
     */
    @GET
    @ClientQueryParam(name = "method", value = "flickr.photos.search")
    @ClientQueryParam(name = "safe_search", value = "1")
    @ClientQueryParam(name = "privacy_filter", value = "1")
    @ClientQueryParam(name = "extras", value = "description,tags,owner_name,date_upload,views,media,o_dims,url_s,url_m,url_l,url_o")
    Uni<FlickrResponse> search(
            @NotNull @QueryParam("text") String query,
            @QueryParam("media") String media,
            @QueryParam("min_upload_date") Integer dateFrom,
            @QueryParam("max_upload_date") Integer dateTill,
            @QueryParam("sort") String sort,
            @QueryParam("page") Integer page,
            @QueryParam("per_page") Integer perPage
    );

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
