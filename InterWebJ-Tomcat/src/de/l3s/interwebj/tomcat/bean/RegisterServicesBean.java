package de.l3s.interwebj.tomcat.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.Engine;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.ServiceConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named
@ViewScoped
public class RegisterServicesBean implements Serializable
{
	private static final long serialVersionUID = 1250989064923740720L;
	private static final Logger log = LogManager.getLogger(RegisterServicesBean.class);

    private List<ConnectorWrapper> connectorWrappers;

    public RegisterServicesBean()
    {
	connectorWrappers = new ArrayList<ConnectorWrapper>();
	Engine engine = Environment.getInstance().getEngine();
	for(ServiceConnector connector : engine.getConnectors())
	{
	    if(connector.isConnectorRegistrationDataRequired())
	    {
		ConnectorWrapper connectorWrapper = new ConnectorWrapper();
		connectorWrapper.setConnector(connector);
		if(connector.getAuthCredentials() != null)
		{
		    connectorWrapper.setKey(connector.getAuthCredentials().getKey());
		    connectorWrapper.setSecret(connector.getAuthCredentials().getSecret());
		}
		connectorWrappers.add(connectorWrapper);
	    }
	}
    }

    public List<ConnectorWrapper> getConnectorWrappers()
    {
	return connectorWrappers;
    }

    public boolean isRegistered(Object obj) throws InterWebException
    {
	ConnectorWrapper connectorWrapper = (ConnectorWrapper) obj;
	return connectorWrapper.getConnector().isRegistered();
    }

    public void register(ConnectorWrapper connectorWrapper)
    {
	try
	{
	    Engine engine = Environment.getInstance().getEngine();
	    AuthCredentials authCredentials = new AuthCredentials(connectorWrapper.getKey(), connectorWrapper.getSecret());
	    ServiceConnector connector = connectorWrapper.getConnector();
	    log.info("registering connector: [" + connector.getName() + "] with credentials: " + authCredentials);
	    connector.setAuthCredentials(authCredentials);
	    engine.setConsumerAuthCredentials(connector.getName(), authCredentials);
	}
	catch(Exception e)
	{
	    log.error(e);
	}
    }

    public void unregister(ConnectorWrapper connectorWrapper)
    {
	try
	{
	    ServiceConnector connector = connectorWrapper.getConnector();
	    log.info("unregistering connector: [" + connector.getName() + "]");
	    Engine engine = Environment.getInstance().getEngine();
	    connector.setAuthCredentials(null);
	    engine.setConsumerAuthCredentials(connector.getName(), null);
	}
	catch(Exception e)
	{
	    log.error(e);
	}
    }

}
