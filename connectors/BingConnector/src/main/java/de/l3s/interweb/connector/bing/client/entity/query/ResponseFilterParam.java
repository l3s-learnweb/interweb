package de.l3s.interweb.connector.bing.client.entity.query;

public enum ResponseFilterParam {

    IMAGES("images"),
    VIDEOS("videos"),
    WEB_PAGES("webpages");

    private final String value;

    ResponseFilterParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
