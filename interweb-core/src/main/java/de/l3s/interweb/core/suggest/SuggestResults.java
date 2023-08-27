package de.l3s.interweb.core.suggest;

import java.util.Collection;

import de.l3s.interweb.core.Results;

public class SuggestResults extends Results<SuggestConnectorResults> {
    public SuggestResults() {
    }

    public SuggestResults(Collection<SuggestConnectorResults> results) {
        super(results);
    }
}
