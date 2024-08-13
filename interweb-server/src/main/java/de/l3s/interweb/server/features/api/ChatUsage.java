package de.l3s.interweb.server.features.api;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class ChatUsage {
    @JsonProperty("input_tokens")
    public Long inputTokens;
    @JsonProperty("output_tokens")
    public Long outputTokens;
    @JsonProperty("estimated_cost")
    public Double estimatedCost;
    @JsonProperty("total_requests")
    public Long totalRequests;

    public ChatUsage(Long inputTokens, Long outputTokens, Double estimatedCost, Long totalRequests) {
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.estimatedCost = estimatedCost;
        this.totalRequests = totalRequests;
    }

    public static Uni<ChatUsage> findByApikey(ApiKey apikey) {
        return ApiChatRequest.find("select sum(inputTokens) as inputTokens, sum(outputTokens) as outputTokens, sum(estimatedCost) as estimatedCost, count(*) as totalRequests from ApiChatRequest where apikey.id = ?1", apikey.id).project(ChatUsage.class).singleResult();
    }
}
