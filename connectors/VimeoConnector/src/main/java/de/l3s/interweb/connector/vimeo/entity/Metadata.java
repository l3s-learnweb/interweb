package de.l3s.interweb.connector.vimeo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metadata {

    @JsonProperty("connections")
    private Connections connections;

    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

}
