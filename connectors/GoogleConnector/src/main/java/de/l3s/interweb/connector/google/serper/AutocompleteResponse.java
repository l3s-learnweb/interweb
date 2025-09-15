package de.l3s.interweb.connector.google.serper;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class AutocompleteResponse {
    private SearchRequest searchParameters;
    private List<Suggestion> suggestions;
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
