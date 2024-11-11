package de.l3s.interweb.server.features.api;

import java.time.Instant;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.server.features.user.User;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageChat {
    @JsonProperty("input_tokens")
    private Long inputTokens;
    @JsonProperty("output_tokens")
    private Long outputTokens;
    @JsonProperty("estimated_cost")
    private Double estimatedCost;
    @JsonProperty("total_requests")
    private Long totalRequests;

    public UsageChat(Long inputTokens, Long outputTokens, Double estimatedCost, Long totalRequests) {
        this.totalRequests = totalRequests;
        if (totalRequests == 0) {
            this.inputTokens = 0L;
            this.outputTokens = 0L;
            this.estimatedCost = 0d;
        } else {
            this.inputTokens = inputTokens;
            this.outputTokens = outputTokens;
            this.estimatedCost = estimatedCost;
        }
    }

    public Long getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(Long inputTokens) {
        this.inputTokens = inputTokens;
    }

    public Long getOutputTokens() {
        return outputTokens;
    }

    public void setOutputTokens(Long outputTokens) {
        this.outputTokens = outputTokens;
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

    public static Uni<UsageChat> findByApikey(ApiKey apikey) {
        return ApiRequestChat.find("""
                select sum(inputTokens) as inputTokens, sum(outputTokens) as outputTokens, sum(estimatedCost) as estimatedCost, count(*) as totalRequests
                from ApiRequestChat
                where apikey.id = ?1
                """, apikey.id)
            .project(UsageChat.class)
            .singleResult();
    }

    public static Uni<UsageChat> findByUser(User user) {
        return ApiRequestChat.find("""
                select sum(inputTokens) as inputTokens, sum(outputTokens) as outputTokens, sum(estimatedCost) as estimatedCost, count(*) as totalRequests
                from ApiRequestChat
                where user.id = ?1
                """, user.id)
            .project(UsageChat.class)
            .singleResult();
    }

    public static Uni<UsageChat> findByUser(User user, Instant start, Instant end) {
        return ApiRequestChat.find("""
                select sum(inputTokens) as inputTokens, sum(outputTokens) as outputTokens, sum(estimatedCost) as estimatedCost, count(*) as totalRequests
                from ApiRequestChat
                where user.id = ?1 and created >= ?2 and created <= ?3
                """, user.id, start, end)
            .project(UsageChat.class)
            .singleResult();
    }
}
