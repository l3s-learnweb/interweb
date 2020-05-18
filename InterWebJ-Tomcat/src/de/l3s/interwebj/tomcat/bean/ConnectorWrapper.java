package de.l3s.interwebj.tomcat.bean;

import de.l3s.interwebj.core.core.ServiceConnector;

public class ConnectorWrapper {
    private ServiceConnector connector;
    private String key;
    private String secret;

    public ServiceConnector getConnector() {
        return connector;
    }

    public void setConnector(ServiceConnector connector) {
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
