package de.l3s.interweb.core.search;

import java.util.Collection;

import de.l3s.interweb.core.Results;

public class SearchResults extends Results<SearchConnectorResults> {
    public SearchResults() {
    }

    public SearchResults(Collection<SearchConnectorResults> results) {
        super(results);
    }
}
