package de.l3s.interweb.core.suggest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.Query;

@RegisterForReflection
public class SuggestQuery extends Query {
    @NotEmpty
    private String query;
    @Size(min = 2, max = 2)
    private String language;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
