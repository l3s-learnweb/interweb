package de.l3s.interweb.tomcat.bean;

import de.l3s.interweb.core.search.SearchProvider;

public class ConnectorWrapper {
    private SearchProvider connector;
    private String key;
    private String secret;

    public SearchProvider getConnector() {
        return connector;
    }

    public void setConnector(SearchProvider connector) {
        this.connector = connector;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

}
