package de.l3s.interweb.server.features.api;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.server.features.user.User;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageSearch {
    @JsonProperty("estimated_cost")
    private Double estimatedCost;
    @JsonProperty("total_requests")
    private Long totalRequests;

    public UsageSearch(Double estimatedCost, Long totalRequests) {
        this.totalRequests = totalRequests;
        if (totalRequests == 0) {
            this.estimatedCost = 0d;
        } else {
            this.estimatedCost = estimatedCost;
        }
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public static Uni<UsageSearch> findByApikey(ApiKey apikey) {
        return ApiRequestSearch.find("""
                select sum(estimatedCost) as estimatedCost, count(*) as totalRequests
                from ApiRequestSearch
                where apikey.id = ?1
                """, apikey.id)
            .project(UsageSearch.class)
            .singleResult();
    }

    public static Uni<UsageSearch> findByApikey(ApiKey apikey, Instant start, Instant end) {
        return ApiRequestSearch.find("""
                select sum(estimatedCost) as estimatedCost, count(*) as totalRequests
                from ApiRequestSearch
                where apikey.id = ?1 and created >= ?2 and created <= ?3
                """, apikey.id, start, end)
            .project(UsageSearch.class)
            .singleResult();
    }

    public static Uni<UsageSearch> findByUser(User user) {
        return ApiRequestSearch.find("""
                select sum(estimatedCost) as estimatedCost, count(*) as totalRequests
                from ApiRequestSearch
                where user.id = ?1
                """, user.id)
            .project(UsageSearch.class)
            .singleResult();
    }

    public static Uni<UsageSearch> findByUser(User user, Instant start, Instant end) {
        return ApiRequestSearch.find("""
                select sum(estimatedCost) as estimatedCost, count(*) as totalRequests
                from ApiRequestSearch
                where user.id = ?1 and created >= ?2 and created <= ?3
                """, user.id, start, end)
            .project(UsageSearch.class)
            .singleResult();
    }
}
