package de.l3s.interweb.connector.vimeo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Related {

    @JsonProperty("uri")
    private String uri;
    @JsonProperty("options")
    private List<String> options = null;

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

}
