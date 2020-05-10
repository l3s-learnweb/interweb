package de.l3s.interwebj.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletContext;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.Engine;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;
import de.l3s.interwebj.webutil.FacesUtils;

@ManagedBean
@ViewScoped
public class RegisterServicesBean
{

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

    public String register(ConnectorWrapper connectorWrapper)
    {
	try
	{
	    Engine engine = Environment.getInstance().getEngine();
	    AuthCredentials authCredentials = new AuthCredentials(connectorWrapper.getKey(), connectorWrapper.getSecret());
	    ServiceConnector connector = connectorWrapper.getConnector();
	    Environment.logger.info("registering connector: [" + connector.getName() + "] with credentials: " + authCredentials);
	    connector.setAuthCredentials(authCredentials);
	    engine.setConsumerAuthCredentials(connector.getName(), authCredentials);
	}
	catch(Exception e)
	{
	    return "failed";
	}
	return "success";
    }

    public String unregister(ConnectorWrapper connectorWrapper)
    {
	try
	{
	    ServiceConnector connector = connectorWrapper.getConnector();
	    Environment.logger.info("unregistering connector: [" + connector.getName() + "]");
	    Engine engine = Environment.getInstance().getEngine();
	    connector.setAuthCredentials(null);
	    engine.setConsumerAuthCredentials(connector.getName(), null);
	}
	catch(Exception e)
	{
	    return "failed";
	}
	return "success";
    }

}
