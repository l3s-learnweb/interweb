package de.l3s.interwebj.connector.vimeo.entity;

import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("plays")
    private Long plays;

    public Long getPlays() {
        return plays;
    }

    public void setPlays(Long plays) {
        this.plays = plays;
    }

}
