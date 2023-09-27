package de.l3s.interweb.core.describe;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.ConnectorResults;
import de.l3s.interweb.core.search.SearchItem;

@RegisterForReflection
public class DescribeResults extends ConnectorResults {
    private final SearchItem entity;

    public DescribeResults() {
        this.entity = new SearchItem();
    }

    public DescribeResults(SearchItem entity) {
        this.entity = entity;
    }

    public SearchItem getEntity() {
        return entity;
    }
}
