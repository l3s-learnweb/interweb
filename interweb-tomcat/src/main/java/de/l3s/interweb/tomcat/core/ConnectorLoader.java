package de.l3s.interweb.tomcat.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.l3s.interweb.core.connector.ServiceConnector;

public class ConnectorLoader {
    private static final Logger log = LogManager.getLogger(ConnectorLoader.class);

    public List<ServiceConnector> load() {
        List<ServiceConnector> connectors = new ArrayList<>();
        log.info("loading connectors");

        ServiceLoader<ServiceConnector> providers = ServiceLoader.load(ServiceConnector.class);
        for (ServiceConnector connector : providers) {
            connectors.add(connector);
        }

        return connectors;
    }
}
