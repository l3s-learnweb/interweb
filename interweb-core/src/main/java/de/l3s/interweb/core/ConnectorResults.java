package de.l3s.interweb.core;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectorResults {
    @JsonProperty("service")
    private String service; // name of the connector
    @JsonProperty("service_url")
    private String serviceUrl;
    @JsonProperty("elapsed_time")
    private long elapsedTime; // time in ms
    @JsonProperty("created")
    private Instant created = Instant.now();
    @JsonProperty("error")
    private String error;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setError(ConnectorException e) {
        setError(e.getMessage());
    }
}
