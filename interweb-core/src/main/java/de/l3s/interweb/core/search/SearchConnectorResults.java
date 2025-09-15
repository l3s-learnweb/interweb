package de.l3s.interweb.core.search;

import java.util.LinkedList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.l3s.interweb.core.ConnectorResults;

@RegisterForReflection
@JsonPropertyOrder({"service", "service_url", "elapsed_time", "items"})
public class SearchConnectorResults extends ConnectorResults {

    @JsonProperty("total_results")
    private Long totalResults;
    @JsonProperty("estimated_cost")
    private Double estimatedCost;
    private final List<SearchItem> items;

    public SearchConnectorResults() {
        this.items = new LinkedList<>();
    }

    public List<SearchItem> getItems() {
        return items;
    }

    public void addResultItem(SearchItem resultItem) {
        items.add(resultItem);
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public void addTotalResults(long totalResultCount) {
        if (this.totalResults == null) {
            this.totalResults = 0L;
        }
        this.totalResults += totalResultCount;
    }
}
