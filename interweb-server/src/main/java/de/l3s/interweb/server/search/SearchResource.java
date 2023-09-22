package de.l3s.interweb.server.search;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestQuery;

import de.l3s.interweb.core.search.*;
import de.l3s.interweb.core.util.StringUtils;

@Path("/search")
public class SearchResource {

    @Inject
    SearchService searchService;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @Authenticated
    public Uni<SearchResults> search(@Parameter(description = "The search query", example = "hello world") @NotEmpty @RestQuery("query") String query,
                                     @Parameter(description = "A content types to search for") @NotEmpty @RestQuery("content_types") ContentType[] contentTypes,
                                     @Parameter(description = "A services to search in") @RestQuery("services") String[] services,
                                     @Parameter(description = "A two letter language code", example = "en, de, es, uk") @RestQuery("lang") String lang,
                                     @RestQuery("extras") SearchExtra[] extras,
                                     @RestQuery("date_from") LocalDate dateFrom,
                                     @RestQuery("date_to") LocalDate dateTo,
                                     @RestQuery("sort") SearchSort sort,
                                     @RestQuery("page") @Max(100) Integer page,
                                     @RestQuery("per_page") @Max(500) Integer perPage,
                                     @Parameter(description = "An external request timeout in ms", example = "1000") @RestQuery("timeout") @Max(500) Integer timeout) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query.trim());
        searchQuery.setContentTypes(contentTypes);
        searchQuery.setServices(StringUtils.toIdSet(services));
        searchQuery.setExtras(extras);
        searchQuery.setDateFrom(dateFrom);
        searchQuery.setDateTo(dateTo);
        searchQuery.setLanguage(lang);
        searchQuery.setSort(sort);
        searchQuery.setPage(page);
        searchQuery.setPerPage(perPage);
        searchQuery.setTimeout(timeout);
        return search(searchQuery);
    }

    @POST
    @Authenticated
    public Uni<SearchResults> search(@Valid SearchQuery query) {
        long start = System.currentTimeMillis();
        return searchService.search(query).map(results -> {
            results.setElapsedTime(System.currentTimeMillis() - start);
            return results;
        });
    }

    @GET
    @Path("/services")
    public Uni<List<ServiceEntity>> services() {
        List<ServiceEntity> services = new ArrayList<>();
        for (SearchConnector service : searchService.getConnectors()) {
            services.add(new ServiceEntity(service.getId(), service.getName(), service.getBaseUrl(), service.getSearchTypes()));
        }
        return Uni.createFrom().item(services);
    }

    public record ServiceEntity(String id, String title, String baseUrl, ContentType[] searchTypes) {
    }
}
