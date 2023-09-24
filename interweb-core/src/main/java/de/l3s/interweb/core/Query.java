package de.l3s.interweb.core;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Query implements Serializable {
    @Serial
    private static final long serialVersionUID = -1510912360277464246L;

    @JsonProperty("services")
    private Set<String> services = new HashSet<>();

    public void setServices(Set<String> services) {
        this.services = services;
    }

    @JsonIgnore
    public void setServices(String ...services) {
        this.services = Set.of(services);
    }

    public Set<String> getServices() {
        return services;
    }
}
