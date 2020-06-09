package de.l3s.interwebj.connector.vimeo.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Related {

    @SerializedName("uri")
    private String uri;
    @SerializedName("options")
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