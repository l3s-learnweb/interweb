package de.l3s.interwebj.bean;

import de.l3s.interwebj.core.ServiceConnector;

public class ConnectorWrapper
{

    private ServiceConnector connector;
    private String key;
    private String secret;

    public ServiceConnector getConnector()
    {
	return connector;
    }

    public String getKey()
    {
	return key;
    }

    public String getSecret()
    {
	return secret;
    }

    public void setConnector(ServiceConnector connector)
    {
	this.connector = connector;
    }

    public void setKey(String key)
    {
	this.key = key;
    }

    public void setSecret(String secret)
    {
	this.secret = secret;
    }

}
