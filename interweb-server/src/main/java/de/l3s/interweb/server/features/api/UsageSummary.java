package de.l3s.interweb.server.features.api;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.smallrye.mutiny.Uni;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.server.features.user.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsageSummary {
    private UsageChat chat;
    private UsageSearch search;

    @JsonProperty("monthly_budget")
    private double monthlyBudget;
    @JsonProperty("monthly_budget_used")
    private double monthlyBudgetUsed;
    @JsonProperty("monthly_budget_remaining")
    private double monthlyBudgetRemaining;

    @JsonProperty("total_used")
    private Double totalUsed;

    public UsageChat getChat() {
        return chat;
    }

    public UsageSearch getSearch() {
        return search;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public double getMonthlyBudgetUsed() {
        return monthlyBudgetUsed;
    }

    public double getMonthlyBudgetRemaining() {
        return monthlyBudgetRemaining;
    }

    public Double getTotalUsed() {
        return totalUsed;
    }

    private static Uni<UsageSummary> combine(User user, Uni<UsageChat> monthlyChatUsage, Uni<UsageSearch> monthlySearchUsage, Uni<UsageChat> totalChatUsage, Uni<UsageSearch> totalSearchUsage) {
        return monthlyChatUsage.flatMap(monthlyChat ->
            monthlySearchUsage.flatMap(monthlySearch ->
                totalChatUsage.flatMap(totalChat ->
                    totalSearchUsage.map(totalSearch ->
                        combine(user, monthlyChat, monthlySearch, totalChat, totalSearch)))));
    }

    private static UsageSummary combine(User user, UsageChat monthlyChat, UsageSearch monthlySearch, UsageChat totalChat, UsageSearch totalSearch) {
        UsageSummary summary = new UsageSummary();
        summary.chat = totalChat;
        summary.search = totalSearch;
        summary.totalUsed = totalChat.getEstimatedCost() + totalSearch.getEstimatedCost();

        summary.monthlyBudget = user == null || user.monthlyBudget == null ? 0 : user.monthlyBudget;
        summary.monthlyBudgetUsed = monthlyChat.getEstimatedCost() + monthlySearch.getEstimatedCost();
        summary.monthlyBudgetRemaining = summary.monthlyBudget - summary.monthlyBudgetUsed;
        return summary;
    }

    public static Uni<UsageSummary> findByApikey(ApiKey apikey) {
        Instant today = Instant.now().plus(1, ChronoUnit.DAYS); // to make sure include all requests from today
        Instant monthsAgo = today.minus(31, ChronoUnit.DAYS);

        final Uni<UsageChat> monthlyChatUsage = UsageChat.findByApikey(apikey, monthsAgo, today);
        final Uni<UsageSearch> monthlySearchUsage = UsageSearch.findByApikey(apikey, monthsAgo, today);
        final Uni<UsageChat> totalChatUsage = UsageChat.findByApikey(apikey);
        final Uni<UsageSearch> totalSearchUsage = UsageSearch.findByApikey(apikey);
        return combine(apikey.user, monthlyChatUsage, monthlySearchUsage, totalChatUsage, totalSearchUsage);
    }

    public static Uni<UsageSummary> findByUser(User user) {
        Instant today = Instant.now().plus(1, ChronoUnit.DAYS); // to make sure include all requests from today
        Instant monthsAgo = today.minus(31, ChronoUnit.DAYS);

        final Uni<UsageChat> monthlyChatUsage = UsageChat.findByUser(user, monthsAgo, today);
        final Uni<UsageSearch> monthlySearchUsage = UsageSearch.findByUser(user, monthsAgo, today);
        final Uni<UsageChat> totalChatUsage = UsageChat.findByUser(user);
        final Uni<UsageSearch> totalSearchUsage = UsageSearch.findByUser(user);
        return combine(user, monthlyChatUsage, monthlySearchUsage, totalChatUsage, totalSearchUsage);
    }
}
