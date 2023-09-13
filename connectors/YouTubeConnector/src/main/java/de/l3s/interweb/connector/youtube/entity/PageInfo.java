package de.l3s.interweb.connector.youtube.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PageInfo(

    @JsonProperty("totalResults")
    Integer totalResults,

    @JsonProperty("resultsPerPage")
    Integer resultsPerPage
) {
}