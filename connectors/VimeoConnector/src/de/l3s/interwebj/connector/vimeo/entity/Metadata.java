package de.l3s.interwebj.connector.vimeo.entity;

import com.google.gson.annotations.SerializedName;

public class Metadata {

    @SerializedName("connections")
    private Connections connections;

    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

}
