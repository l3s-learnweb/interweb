package de.l3s.interweb.core.search;

import java.util.LinkedList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.ConnectorResults;

@RegisterForReflection
public class SearchConnectorResults extends ConnectorResults {

    @JsonProperty("total_results")
    private long totalResults = 0;
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

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public void addTotalResults(long totalResultCount) {
        this.totalResults += totalResultCount;
    }
}
