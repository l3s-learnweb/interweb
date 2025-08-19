package de.l3s.interweb.connector.serper.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Suggestion {

    @JsonProperty("value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
