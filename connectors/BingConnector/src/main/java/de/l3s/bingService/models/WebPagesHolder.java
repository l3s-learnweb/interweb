package de.l3s.bingService.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WebPagesHolder extends BaseHolder {

    @SerializedName("value")
    private List<WebPage> values;

    public List<WebPage> getValues() {
        return values;
    }

    public void setValues(List<WebPage> values) {
        this.values = values;
    }
}
