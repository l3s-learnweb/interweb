package de.l3s.interwebj.tomcat.bean;

import de.l3s.interwebj.core.core.ServiceConnector;

import java.io.Serializable;

public class ConnectorWrapper implements Serializable
{
    private static final long serialVersionUID = 8378143922412889383L;

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
