package de.l3s.interweb.connector.vimeo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Stats {

    @JsonProperty("plays")
    private Long plays;

    public Long getPlays() {
        return plays;
    }

    public void setPlays(Long plays) {
        this.plays = plays;
    }

}
