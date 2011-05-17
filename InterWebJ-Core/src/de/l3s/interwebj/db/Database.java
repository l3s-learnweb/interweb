package de.l3s.interwebj.db;


import java.util.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;


public interface Database
{
	
	public InterWebPrincipal authenticate(String userName, String userPassword);
	

	public void close();
	

	public void deleteConnector(String connectorName);
	

	public void deleteConsumer(String userName, String consumerName);
	

	public boolean hasPrincipal(String userName);
	

	public AuthCredentials readConnectorAuthCredentials(String connectorName);
	

	public Consumer readConsumerByKey(String key);
	

	public List<Consumer> readConsumers(String userName);
	

	public InterWebPrincipal readPrincipalByName(String name);
	

	public InterWebPrincipal readPrincipalByKey(String key);
	

	public AuthCredentials readUserAuthCredentials(String connectorName,
	                                               String userName);
	

	public void saveConnector(String connectorName,
	                          AuthCredentials authCredentials);
	

	public void saveConsumer(String userName, Consumer consumer);
	

	public void savePrincipal(InterWebPrincipal principal, String password);
	

	public void saveUserAuthCredentials(String connectorName,
	                                    String userName,
	                                    AuthCredentials authCredentials);
	

	public void updatePrincipal(InterWebPrincipal principal);
	
}
