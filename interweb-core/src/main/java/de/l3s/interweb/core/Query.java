package de.l3s.interweb.core;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Query implements Serializable {
    @Serial
    private static final long serialVersionUID = -1510912360277464246L;

    private Set<String> services = new HashSet<>();

    public void setServices(Set<String> services) {
        this.services = services;
    }

    public void setServices(String ...services) {
        this.services = Set.of(services);
    }

    public Set<String> getServices() {
        return services;
    }
}
