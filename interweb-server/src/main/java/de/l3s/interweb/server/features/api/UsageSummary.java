package de.l3s.interweb.server.features.api;

import java.time.Instant;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.server.features.user.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageSummary {
    private UsageChat chat;
    private UsageSearch search;
    @JsonProperty("monthly_budget")
    private Double monthlyBudget;
    @JsonProperty("monthly_budget_used")
    private Double monthlyBudgetUsed;
    @JsonProperty("monthly_budget_remaining")
    private Double monthlyBudgetRemaining;

    public UsageChat getChat() {
        return chat;
    }

    public UsageSearch getSearch() {
        return search;
    }

    public Double getMonthlyBudget() {
        return monthlyBudget;
    }

    public Double getMonthlyBudgetUsed() {
        return monthlyBudgetUsed;
    }

    public Double getMonthlyBudgetRemaining() {
        return monthlyBudgetRemaining;
    }

    private static Uni<UsageSummary> combine(User user, Uni<UsageChat> usageChat, Uni<UsageSearch> usageSearch) {
        return usageChat.chain(chat -> usageSearch.map(search -> {
            UsageSummary summary = new UsageSummary();
            summary.chat = chat;
            summary.search = search;
            summary.monthlyBudget = user.monthlyBudget;
            summary.monthlyBudgetUsed = summary.chat.getEstimatedCost() + summary.search.getEstimatedCost();
            summary.monthlyBudgetRemaining = summary.monthlyBudget - summary.monthlyBudgetUsed;
            return summary;
        }));
    }

    @WithSession
    public static Uni<UsageSummary> findByApikey(ApiKey apikey) {
        final Uni<UsageChat> usageChat = UsageChat.findByApikey(apikey);
        final Uni<UsageSearch> usageSearch = UsageSearch.findByApikey(apikey);
        return combine(apikey.user, usageChat, usageSearch);
    }

    @WithSession
    public static Uni<UsageSummary> findByUser(User user) {
        final Uni<UsageChat> usageChat = UsageChat.findByUser(user);
        final Uni<UsageSearch> usageSearch = UsageSearch.findByUser(user);
        return combine(user, usageChat, usageSearch);
    }

    @WithSession
    public static Uni<UsageSummary> findByUser(User user, Instant start, Instant end) {
        final Uni<UsageChat> usageChat = UsageChat.findByUser(user, start, end);
        final Uni<UsageSearch> usageSearch = UsageSearch.findByUser(user, start, end);
        return combine(user, usageChat, usageSearch);
    }
}
