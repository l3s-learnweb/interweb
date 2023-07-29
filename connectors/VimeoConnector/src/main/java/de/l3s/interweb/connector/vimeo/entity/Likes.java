package de.l3s.interweb.connector.vimeo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Likes {

    @JsonProperty("uri")
    private String uri;
    @JsonProperty("options")
    private List<String> options = null;
    @JsonProperty("total")
    private Integer total;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
