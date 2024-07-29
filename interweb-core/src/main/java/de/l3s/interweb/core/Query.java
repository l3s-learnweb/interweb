package de.l3s.interweb.core;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = false)
public class Query implements Serializable {
    @Serial
    private static final long serialVersionUID = -1510912360277464246L;

    @JsonProperty("services")
    private Set<String> services = new HashSet<>();

    @Min(100)
    @Max(600000)
    @JsonProperty("timeout")
    private Integer timeout;

    @JsonIgnore
    private boolean ignoreCache;

    public void setServices(Set<String> services) {
        this.services = services;
    }

    @JsonIgnore
    public void setServices(String... services) {
        this.services = Set.of(services);
    }

    public Set<String> getServices() {
        return services;
    }

    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout in milliseconds used for the API call, default is 10 seconds
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public boolean getIgnoreCache() {
        return ignoreCache;
    }

    public void setIgnoreCache(boolean ignoreCache) {
        this.ignoreCache = ignoreCache;
    }
}
