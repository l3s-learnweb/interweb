package de.l3s.interweb.connector.google.serper;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AutocompleteResponse {

    @JsonProperty("searchParameters")
    private SearchRequest searchParameters;

    @JsonProperty("suggestions")
    private List<Suggestion> suggestions;

    @JsonProperty("credits")
    private Integer credits;

    public SearchRequest getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(SearchRequest searchParameters) {
        this.searchParameters = searchParameters;
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }
}
