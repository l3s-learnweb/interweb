package de.l3s.bingService.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class VideoHolder extends BaseHolder {

    @SerializedName("value")
    private List<Video> values;

    public List<Video> getValues() {
        return values;
    }

    public void setValues(List<Video> values) {
        this.values = values;
    }

}
