package de.l3s.interweb.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "elapsed_time", "query", "results" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Results<T extends ConnectorResults> {
    /**
     * Time in ms that the request took to complete.
     */
    @JsonProperty("elapsed_time")
    private long elapsedTime;

    /**
     * The query that was used to generate the results.
     */
    @JsonProperty("query")
    private Query query;

    /**
     * The list of results from different connectors.
     */
    @JsonProperty("results")
    private final List<T> results = new LinkedList<>();

    public Results() {
    }

    public Results(Collection<T> results) {
        add(results);
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void add(T result) {
        results.add(result);
    }

    public void add(Collection<T> list) {
        results.addAll(list);
    }

    public List<T> getResults() {
        return results;
    }
}
