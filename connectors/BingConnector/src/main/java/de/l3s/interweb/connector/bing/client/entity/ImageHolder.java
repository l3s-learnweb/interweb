package de.l3s.interweb.connector.bing.client.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageHolder extends BaseHolder {

    @JsonProperty("value")
    private List<Image> values;

    public List<Image> getValues() {
        return values;
    }

    public void setValues(List<Image> values) {
        this.values = values;
    }
}
