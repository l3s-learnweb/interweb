package de.l3s.interweb.core;

public interface Connector {

    String getName();

    String getBaseUrl();

    default String getId() {
        return getName().toLowerCase();
    }

    default void fillResult(ConnectorResults results, long elapsedTime) {
        results.setService(getName());
        results.setServiceUrl(getBaseUrl());
        results.setElapsedTime(elapsedTime);
    }
}
