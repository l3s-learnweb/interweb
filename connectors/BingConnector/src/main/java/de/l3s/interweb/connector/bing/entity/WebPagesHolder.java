package de.l3s.interweb.connector.bing.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebPagesHolder extends BaseHolder {

    @JsonProperty("value")
    private List<WebPage> values;

    public List<WebPage> getValues() {
        return values;
    }

    public void setValues(List<WebPage> values) {
        this.values = values;
    }
}
