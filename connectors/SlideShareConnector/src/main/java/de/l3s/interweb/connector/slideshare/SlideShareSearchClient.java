package de.l3s.interweb.connector.slideshare;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.ClientFormParam;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.core.ConnectorException;

@Path("/api/2")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_XML)
@ClientQueryParam(name = "detailed", value = "1")
@RegisterRestClient(configKey = "slideshare")
public interface SlideShareSearchClient {

    /**
     * Slideshow Search
     * https://www.slideshare.net/developers/documentation#search_slideshows
     *
     * @param query the query string
     * @param page The page number of the results (works in conjunction with items_per_page), default is 1
     * @param itemsPerPage Number of results to return per page, default is 12, the maximum is 50.
     * @param sort Sort order (default is 'relevance') ('mostviewed','mostdownloaded','latest')
     * @param lang Language of slideshows (default is English, 'en')
     *             ('**':All,'es':Spanish,'pt':Portuguese,'fr':French,'it':Italian,'nl':Dutch, 'de':German,'zh':Chinese,'ja':Japanese,'ko':Korean,'ro':Romanian, '!!':Other)
     * @param fileTpe File type to search for. Default is "all". ('presentations', 'documents', 'webinars','videos', 'infographics')
     * @param uploadDate The time period you want to restrict your search to. 'week' would restrict to the last week. (default is 'any') ('week', 'month', 'year')
     */
    @POST
    @Path("/search_slideshows")
    @ClientFormParam(name = "api_key", value = "${connector.slideshare.apikey}")
    Uni<String> search(
        @NotNull @QueryParam("q") String query,
        @QueryParam("page") Integer page,
        @QueryParam("items_per_page") Integer itemsPerPage,
        @QueryParam("sort") String sort,
        @QueryParam("lang") String lang,
        @QueryParam("file_type") String fileTpe,
        @QueryParam("upload_date") String uploadDate,

        @FormParam("ts") Long timestamp,
        @FormParam("hash") String hash
    );

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
