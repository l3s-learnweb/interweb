package de.l3s.interweb.server.features.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.server.features.user.ApiKey;

@RegisterForReflection
public class ChatsStats {
    @JsonProperty("used_tokens")
    public Long usedTokens;
    @JsonProperty("estimated_cost")
    public Double estimatedCost;
    @JsonProperty("total_chats")
    public Long totalChats;

    public ChatsStats(Long usedTokens, Double estimatedCost, Long totalChats) {
        this.usedTokens = usedTokens;
        this.estimatedCost = estimatedCost;
        this.totalChats = totalChats;
    }

    public static Uni<ChatsStats> findByApikey(ApiKey apikey) {
        return Chat.find("select sum(usedTokens) as usedTokens, sum(estimatedCost) as estimatedCost, count(*) as totalChats from Chat where apikey.id = ?1", apikey.id).project(ChatsStats.class).singleResult();
    }
}
