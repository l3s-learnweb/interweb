package de.l3s.interweb.connector.vimeo.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Pictures {

    @SerializedName("uri")
    private String uri;
    @SerializedName("active")
    private Boolean active;
    @SerializedName("type")
    private String type;
    @SerializedName("sizes")
    private List<Size> sizes = null;
    @SerializedName("resource_key")
    private String resourceKey;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

}
