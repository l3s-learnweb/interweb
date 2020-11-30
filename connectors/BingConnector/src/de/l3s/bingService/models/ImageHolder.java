package de.l3s.bingService.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ImageHolder extends BaseHolder {

    @SerializedName("value")
    private List<Image> values;

    public List<Image> getValues() {
        return values;
    }

    public void setValues(List<Image> values) {
        this.values = values;
    }
}
