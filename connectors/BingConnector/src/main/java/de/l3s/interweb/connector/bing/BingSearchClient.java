package de.l3s.interweb.connector.bing;

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
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.l3s.interweb.connector.bing.entity.BingResponse;
import de.l3s.interweb.core.ConnectorException;

@Path("/v7.0")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "bing-search")
@ClientHeaderParam(name = "Ocp-Apim-Subscription-Key", value = "${connector.bing.apikey}")
@ClientQueryParam(name = "safeSearch", value = "Strict")
@ClientQueryParam(name = "textFormat", value = "HTML")
@ClientQueryParam(name = "textDecorations", value = "true")
public interface BingSearchClient {

    enum ResponseFilter {
        computation,
        entities,
        images,
        news,
        places,
        relatedSearches,
        spellSuggestions,
        timezone,
        translations,
        videos,
        webpages
    }

    /**
     * Bing Web Search API
     * https://learn.microsoft.com/en-us/bing/search-apis/bing-web-search/reference/query-parameters
     *
     * @param query The user's search query term. The term may not be empty. The term may contain Bing Advanced Operators.
     *              https://support.microsoft.com/topic/advanced-search-options-b92e25f1-0085-4271-bdf9-14aaea720930
     *              For example, to limit results to a specific domain, use the site: operator (q=fishing+site:fishing.contoso.com).
     * @param count The number of images to return in the response. The default is 10 and the maximum value is 50. The actual number may be less than requested.
     * @param offset The zero-based offset that indicates the number of search results to skip before returning results. The default is 0.
     * @param language The language to use for user interface strings. Bing defaults to en (English).
     *                 You may specify the language using either a 2-letter or 4-letter code. Using 4-letter codes is preferred.
     * @param market The market where the results come from. The market must be in the form <language>-<country/region>.
     * @param freshness Filter search results by the following case-insensitive age values: day, week, month.
     *                  To get articles discovered by Bing during a specific timeframe, specify a date range in the form, YYYY-MM-DD..YYYY-MM-DD.
     *                  To limit the results to a single date, set this parameter to a specific date. For example, &freshness=2019-02-04.
     * @param responseFilter A comma-delimited list of answers to include in the response. If you do not specify this parameter,
     *                       the response includes all search answers for which there's relevant data. Exclude them by prefixing a hyphen (minus) to the value.
     */
    @GET
    @Path("/search")
    Uni<BingResponse> search(
        @NotNull @QueryParam("q") String query,
        @QueryParam("count") Integer count,
        @QueryParam("offset") Integer offset,
        @QueryParam("setLang") String language,
        @QueryParam("mkt") String market,
        @QueryParam("freshness") String freshness,
        @QueryParam("responseFilter") String responseFilter
    );

    /**
     * Bing Image Search API
     * https://learn.microsoft.com/en-us/bing/search-apis/bing-image-search/reference/query-parameters
     *
     * @param query The user's search query term. The term may not be empty. The term may contain Bing Advanced Operators.
     *              https://support.microsoft.com/topic/advanced-search-options-b92e25f1-0085-4271-bdf9-14aaea720930
     *              For example, to limit results to a specific domain, use the site: operator (q=fishing+site:fishing.contoso.com).
     * @param count The number of images to return in the response. The default is 35 and the maximum is 150. The actual number may be less than requested.
     * @param offset The zero-based offset that indicates the number of search results to skip before returning results. The default is 0.
     * @param language The language to use for user interface strings. Bing defaults to en (English).
     *                 You may specify the language using either a 2-letter or 4-letter code. Using 4-letter codes is preferred.
     * @param market The market where the results come from. The market must be in the form <language>-<country/region>.
     * @param freshness Filter search results by the following case-insensitive age values: day, week, month.
     */
    @GET
    @Path("/images/search")
    Uni<BingResponse> searchImages(
        @NotNull @QueryParam("q") String query,
        @QueryParam("count") Integer count,
        @QueryParam("offset") Integer offset,
        @QueryParam("setLang") String language,
        @QueryParam("mkt") String market,
        @QueryParam("freshness") String freshness
    );

    /**
     * Bing Image Search API
     * https://learn.microsoft.com/en-us/bing/search-apis/bing-video-search/reference/query-parameters
     *
     * @param query The user's search query term. The term may not be empty. The term may contain Bing Advanced Operators.
     *              https://support.microsoft.com/topic/advanced-search-options-b92e25f1-0085-4271-bdf9-14aaea720930
     *              For example, to limit results to a specific domain, use the site: operator (q=fishing+site:fishing.contoso.com).
     * @param count The number of images to return in the response. The default is 35 and the maximum is 105. The actual number may be less than requested.
     * @param offset The zero-based offset that indicates the number of search results to skip before returning results. The default is 0.
     * @param language The language to use for user interface strings. Bing defaults to en (English).
     *                 You may specify the language using either a 2-letter or 4-letter code. Using 4-letter codes is preferred.
     * @param market The market where the results come from. The market must be in the form <language>-<country/region>.
     * @param freshness Filter search results by the following case-insensitive age values: day, week, month.
     */
    @GET
    @Path("/videos/search")
    @ClientQueryParam(name = "pricing", value = "free")
    Uni<BingResponse> searchVideos(
        @NotNull @QueryParam("q") String query,
        @QueryParam("count") Integer count,
        @QueryParam("offset") Integer offset,
        @QueryParam("setLang") String language,
        @QueryParam("mkt") String market,
        @QueryParam("freshness") String freshness
    );

    @ClientExceptionMapper
    static RuntimeException toException(Response response) {
        if (response.getStatus() == 429) {
            return new ConnectorException("Rate exceeded");
        }

        return new ConnectorException("Remote service responded with HTTP " + response.getStatus(), response.readEntity(String.class));
    }
}
