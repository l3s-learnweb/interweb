package de.l3s.interweb.connector.bing.client.entity.query;

public enum SafesearchParam {

    OFF("off"),
    MODERATE("moderate"),
    STRICT("strict");

    private final String value;

    SafesearchParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
