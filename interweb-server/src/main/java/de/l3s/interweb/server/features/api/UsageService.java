package de.l3s.interweb.server.features.api;

import javax.naming.LimitExceededException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;

import de.l3s.interweb.server.features.user.User;

@ApplicationScoped
public class UsageService {

    @Inject
    @CacheName("usage")
    Cache cache;

    private Uni<UsageValue> findValue(User user) {
        return cache.as(CaffeineCache.class).getAsync(user.id, k -> {
            Instant today = Instant.now().plus(1, ChronoUnit.DAYS); // to make sure include all requests from today
            Instant monthsAgo = today.minus(31, ChronoUnit.DAYS);

            return UsageSummary.findByUser(user, monthsAgo, today).map(summary -> {
                UsageValue value = new UsageValue();
                value.remaining = summary.getMonthlyBudgetRemaining();
                return value;
            });
        });
    }

    /**
     * Check if a user has enough quota to allocate, if not, throw LimitExceededException.
     */
    public Uni<Void> allocate(User user) {
        return findValue(user).chain(usageValue -> {
            if (usageValue.remaining < 0) {
                return Uni.createFrom().failure(new LimitExceededException());
            }
            return Uni.createFrom().voidItem();
        });
    }

    public Uni<Double> deduct(User user, double cost) {
        return findValue(user).map(usageValue -> {
            usageValue.remaining = usageValue.remaining - cost;
            return usageValue.remaining;
        });
    }

    @WithSession
    @ConsumeEvent("api-request-chat")
    public Uni<Void> consumeChatRequest(ApiRequestChat request) {
        return request.persistAndFlush().eventually(() -> deduct(request.user, request.estimatedCost)).replaceWithVoid();
    }

    @WithSession
    @ConsumeEvent("api-request-search")
    public Uni<Void> consumeSearchRequest(ApiRequestSearch request) {
        return request.persistAndFlush().eventually(() -> deduct(request.user, request.estimatedCost)).replaceWithVoid();
    }

    private static class UsageValue {
        double remaining;
    }

}
