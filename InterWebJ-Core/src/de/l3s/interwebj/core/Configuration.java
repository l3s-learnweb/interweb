package de.l3s.interwebj.core;


import java.net.*;

import org.apache.commons.configuration.*;


public class Configuration
{
	
	private XMLConfiguration configuration;
	

	public Configuration(URL configUrl)
	{
		try
		{
			configuration = (XMLConfiguration) new XMLConfiguration(configUrl).interpolatedConfiguration();
		}
		catch (Exception e)
		{
			System.out.println("Unable to load configuration file by URL ["
			                   + configUrl + "]");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	

	public String getProperty(String key)
	{
		return configuration.getProperty(key).toString();
	}
	
}
