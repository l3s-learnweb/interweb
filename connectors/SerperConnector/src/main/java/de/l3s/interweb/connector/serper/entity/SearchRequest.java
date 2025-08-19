package de.l3s.interweb.connector.serper.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRequest {
    public enum DateRange {
        AnyTime(""),
        PastHour("qdr:h"),
        Past24Hours("qdr:d"),
        PastWeek("qdr:w"),
        PastMonth("qdr:m"),
        PastYear("qdr:y");

        private final String value;

        DateRange(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private String type;
    @JsonProperty("q")
    private String query;
    @JsonProperty("gl")
    private String country;
    @JsonProperty("location")
    private String location;
    @JsonProperty("hl")
    private String language;
    @JsonProperty("tbs")
    private DateRange dateRange;
    @JsonProperty("autocorrect")
    private Boolean autocorrect;
    @JsonProperty("num")
    private Integer results;
    @JsonProperty("page")
    private Integer page;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public Boolean getAutocorrect() {
        return autocorrect;
    }

    public void setAutocorrect(Boolean autocorrect) {
        this.autocorrect = autocorrect;
    }

    public Integer getResults() {
        return results;
    }

    public void setResults(Integer results) {
        this.results = results;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
