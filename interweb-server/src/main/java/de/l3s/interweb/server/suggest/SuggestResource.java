package de.l3s.interweb.server.suggest;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestQuery;

import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestQuery;
import de.l3s.interweb.core.suggest.SuggestResults;
import de.l3s.interweb.core.util.StringUtils;

@Path("/suggest")
public class SuggestResource {

    @Inject
    SuggestService suggestService;

    @GET
    @Authenticated
    public Uni<SuggestResults> suggest(@NotEmpty @RestQuery String q,
                                       @Size(min = 2, max = 2) @RestQuery String lang,
                                       @RestQuery String services) {
        SuggestQuery query = new SuggestQuery();
        query.setQuery(q);
        if (StringUtils.isNotEmpty(lang)) {
            query.setLanguage(lang);
        }
        if (StringUtils.isNotEmpty(services)) {
            query.setServices(StringUtils.toIdSet(services));
        }
        return suggest(query);
    }

    @POST
    @Authenticated
    public Uni<SuggestResults> suggest(@Valid SuggestQuery query) {
        long start = System.currentTimeMillis();
        suggestService.validateServices(query.getServices());
        return suggestService.suggest(query).map(results -> {
            results.setElapsedTime(System.currentTimeMillis() - start);
            return results;
        });
    }

    @GET
    @PermitAll
    @Path("/services")
    public Uni<List<ServiceEntity>> services() {
        List<ServiceEntity> services = new ArrayList<>();
        for (SuggestConnector service : suggestService.getConnectors()) {
            services.add(new ServiceEntity(service.getId(), service.getName(), service.getBaseUrl()));
        }
        return Uni.createFrom().item(services);
    }

    public record ServiceEntity(String id, String title, String baseUrl) {
    }
}
