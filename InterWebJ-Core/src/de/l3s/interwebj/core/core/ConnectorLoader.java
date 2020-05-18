package de.l3s.interwebj.core.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectorLoader {
    private static final Logger log = LogManager.getLogger(ConnectorLoader.class);

    private static final String[] CONNECTORS = {
        "de.l3s.interwebj.connector.bing.BingConnector",
        "de.l3s.interwebj.connector.flickr.FlickrConnector",
        "de.l3s.interwebj.connector.ipernity.IpernityConnector",
        "de.l3s.interwebj.connector.slideshare.SlideShareConnector",
        "de.l3s.interwebj.connector.vimeo.VimeoConnector",
        "de.l3s.interwebj.connector.youtube.YouTubeConnector",
    };

    public List<ServiceConnector> load() {
        List<ServiceConnector> connectors = new ArrayList<ServiceConnector>();
        log.info("loading connectors");
        for (String connectorClassName : CONNECTORS) {
            ServiceConnector connector = loadConnector(connectorClassName);
            if (connector != null) {
                connectors.add(connector);
            }
        }
        return connectors;
    }

    private ServiceConnector loadConnector(String className) {
        log.info("trying load connector: [" + className + "]");
        try {
            ServiceConnector connector = instantiate(className, ServiceConnector.class);
            log.info("Connector [" + connector.getName() + "] successfully loaded");
            return connector;
        } catch (ReflectiveOperationException e) {
            log.error("No class found for connector [" + className + "]", e);
            return null;
        }
    }

    private <T> T instantiate(final String className, final Class<T> type) throws ReflectiveOperationException {
        return type.cast(Class.forName(className).getConstructor().newInstance());
    }
}
