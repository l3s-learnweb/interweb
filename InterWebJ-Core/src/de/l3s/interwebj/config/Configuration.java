package de.l3s.interwebj.config;


import java.io.*;
import java.util.*;

import org.apache.commons.configuration.*;


public class Configuration
{
	
	private XMLConfiguration configuration;
	

	public Configuration(InputStream is)
	    throws ConfigurationException
	{
		configuration = (XMLConfiguration) new XMLConfiguration().interpolatedConfiguration();
		configuration.load(is);
	}
	

	@SuppressWarnings("unchecked")
	public List<String> getProperties(String key)
	{
		return configuration.getList(key, new ArrayList<String>());
	}
	

	public String getProperty(String key)
	{
		return configuration.getProperty(key).toString();
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		Configuration configuration = new Configuration(new FileInputStream("./temp/connector-config.xml"));
		System.out.println(configuration.getProperty("class"));
		System.out.println(configuration.getProperty("name"));
		System.out.println(configuration.getProperty("base-url"));
		System.out.println(configuration.getProperties("content-types.content-type"));
		System.out.println(configuration.getProperties("properties.property.name"));
	}
	
}
