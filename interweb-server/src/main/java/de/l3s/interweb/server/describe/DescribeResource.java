package de.l3s.interweb.server.describe;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import io.quarkus.cache.CacheResult;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestQuery;

import de.l3s.interweb.core.describe.DescribeConnector;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
import de.l3s.interweb.core.util.StringUtils;

@Path("/describe")
public class DescribeResource {

    @Inject
    DescribeService describeService;

    @GET
    @Authenticated
    public Uni<DescribeResults> describe(@RestQuery String link,
                                         @RestQuery String id,
                                         @RestQuery String services) {
        DescribeQuery query = new DescribeQuery();
        query.setId(id);
        query.setLink(link);
        if (StringUtils.isNotEmpty(services)) {
            query.setServices(StringUtils.toIdSet(services));
        }
        return describe(query);
    }

    @POST
    @Authenticated
    @CacheResult(cacheName = "describe")
    public Uni<DescribeResults> describe(@Valid DescribeQuery query) {
        describeService.validateServices(query.getServices());
        if (StringUtils.isEmpty(query.getLink()) && (StringUtils.isEmpty(query.getId()) || query.getServices().isEmpty())) {
            throw new ValidationException("Either `link` or `id` and `services` must be set.");
        }
        if (query.getServices().size() > 1) {
            throw new ValidationException("Only one service is allowed, if you not sure, use `link` parameter instead.");
        }
        return describeService.describe(query);
    }

    @GET
    @PermitAll
    @Path("/services")
    public Uni<List<ServiceEntity>> services() {
        List<ServiceEntity> services = new ArrayList<>();
        for (DescribeConnector service : describeService.getConnectors()) {
            services.add(new ServiceEntity(service.getId(), service.getName(), service.getBaseUrl(), service.getLinkPattern().pattern()));
        }
        return Uni.createFrom().item(services);
    }

    public record ServiceEntity(String id, String title, String baseUrl, String pattern) {
    }
}
