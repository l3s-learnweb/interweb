package de.l3s.interweb.connector.bing.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Publisher {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
