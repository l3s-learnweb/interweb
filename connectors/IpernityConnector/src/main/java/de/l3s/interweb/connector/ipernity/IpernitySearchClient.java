package de.l3s.interweb.connector.ipernity;

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

import de.l3s.interweb.core.ConnectorException;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "ipernity")
@ClientQueryParam(name = "api_key", value = "${connector.ipernity.apikey}")
public interface IpernitySearchClient {

    /**
     * Ipernity Search API
     * http://www.ipernity.com/help/api/method/doc.search
     *
     * @param query      Search document titles, descriptions and tags using a fulltext query. (Use +/- for boolean mode)
     * @param media      Specify the type of returned documents. media values are : photo, audio, video, other.
     * @param page       The page of results to return.
     * @param perPage    The number of docs to return on each page. (Default is 20, maximum is 100)
     * @param sort       The order in which to sort returned documents. The default order is relevance.
     *                   The possible sorts are: relevance, popular, posted-desc, posted-asc, created-desc and created-asc.
     * @param createdMin Specify a minimum posted GMT+0 timestamp.
     * @param createdMax Specify a maximum posted GMT+0 timestamp.
     */
    @GET
    @Path("/doc.search/json")
    @ClientQueryParam(name = "share", value = "4")
    @ClientQueryParam(name = "thumbsize", value = "1024") // 75x, 100, 240, 250x, 500, 560, 640, 800, 1024, 1600 or 2048
    @ClientQueryParam(name = "extra", value = "count,dates") // owner, dates, count, license, medias, geo, original
    Uni<String> search( // because the response is text/plain, we can not read it as JSON
            @NotNull @QueryParam("text") String query,
            @QueryParam("media") String media,
            @QueryParam("page") Integer page,
            @QueryParam("per_page") Integer perPage,
            @QueryParam("sort") String sort,
            @QueryParam("created_min") Integer createdMin,
            @QueryParam("posted_max") Integer createdMax
    );

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
