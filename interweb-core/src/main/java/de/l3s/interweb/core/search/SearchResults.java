package de.l3s.interweb.core.search;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import de.l3s.interweb.core.query.Query;

public class SearchResults implements Serializable {
    @Serial
    private static final long serialVersionUID = -2762679444319967129L;

    @XmlElement(name = "query")
    private final Query query;
    @XmlElement(name = "source")
    private final String source;
    @JsonbProperty("total_results")
    @XmlElement(name = "total_results")
    private long totalResults = 0;
    @JsonbProperty("results")
    @XmlElementWrapper(name = "results")
    @XmlElement(name = "result")
    private final List<SearchItem> items;

    public SearchResults(Query query, String source) {
        this.query = query;
        this.source = source;
        this.items = new LinkedList<>();
    }

    public void addResultItem(SearchItem resultItem) {
        items.add(resultItem);
    }

    public Query getQuery() {
        return query;
    }

    public String getSource() {
        return source;
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

    public List<SearchItem> getItems() {
        return items;
    }

    public int size() {
        return items.size();
    }
}
