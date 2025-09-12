package de.l3s.interweb.connector.google.serper;

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
