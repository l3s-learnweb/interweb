package de.l3s.interweb.core.suggest;

import java.util.Collection;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.Results;

@RegisterForReflection
public class SuggestResults extends Results<SuggestConnectorResults> {
    public SuggestResults() {
    }

    public SuggestResults(Collection<SuggestConnectorResults> results) {
        super(results);
    }
}
