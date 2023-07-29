package de.l3s.interweb.connector.ipernity.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Docs {
    public Integer total;
    @JsonProperty("per_page")
    public Integer perPage;
    public Integer page;
    public Integer pages;
    public List<Doc> doc;
}
