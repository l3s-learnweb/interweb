package de.l3s.interwebj.connector.vimeo.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Credits {

    @SerializedName("uri")
    private String uri;
    @SerializedName("options")
    private List<String> options = null;
    @SerializedName("total")
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
