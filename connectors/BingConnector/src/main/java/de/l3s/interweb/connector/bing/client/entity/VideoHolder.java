package de.l3s.interweb.connector.bing.client.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class VideoHolder extends BaseHolder {

    @JsonProperty("value")
    private List<Video> values;

    public List<Video> getValues() {
        return values;
    }

    public void setValues(List<Video> values) {
        this.values = values;
    }

}
