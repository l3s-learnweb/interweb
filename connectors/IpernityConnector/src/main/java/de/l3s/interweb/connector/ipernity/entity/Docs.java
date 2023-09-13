package de.l3s.interweb.connector.ipernity.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Docs(
    @JsonProperty("pages")
    String pages,

    @JsonProperty("doc")
    List<Doc> doc,

    @JsonProperty("page")
    Integer page,

    @JsonProperty("per_page")
    Integer perPage,

    @JsonProperty("total")
    Integer total
) {
}