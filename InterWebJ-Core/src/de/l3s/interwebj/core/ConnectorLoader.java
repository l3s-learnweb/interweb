package de.l3s.interwebj.core;

import java.util.ArrayList;
import java.util.List;

public class ConnectorLoader
{
    private static final String[] CONNECTORS = {
            "de.l3s.interwebj.connector.bingAzure.BingAzureConnector",
            "de.l3s.interwebj.connector.flickr.FlickrConnector",
            "de.l3s.interwebj.connector.ipernity.IpernityConnector",
            "de.l3s.interwebj.connector.slideshare.SlideShareConnector",
            "de.l3s.interwebj.connector.vimeo.VimeoConnector",
            "de.l3s.interwebj.connector.youtube.YouTubeConnector",
    };

    public List<ServiceConnector> load()
    {
        List<ServiceConnector> connectors = new ArrayList<ServiceConnector>();
        Environment.logger.info("loading connectors");
        for (String connectorClassName : CONNECTORS) {
            ServiceConnector connector = loadConnector(connectorClassName);
            if (connector != null) {
                connectors.add(connector);
            }
        }
        return connectors;
    }

    private ServiceConnector loadConnector(String className)
    {
        Environment.logger.info("trying load connector: [" + className + "]");
        try
        {
            ServiceConnector connector = instantiate(className, ServiceConnector.class);
            Environment.logger.info("Connector [" + connector.getName() + "] successfully loaded");
            return connector;
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
            Environment.logger.severe("No class found for connector [" + className + "]");
            return null;
        }
    }

    private  <T> T instantiate(final String className, final Class<T> type) throws ReflectiveOperationException
    {
        return type.cast(Class.forName(className).getConstructor().newInstance());
    }
}
