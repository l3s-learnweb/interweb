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
	

	@SuppressWarnings({"rawtypes", "unchecked"})
	public String getPropertyValue(String propertiesBase, String propertyName)
	{
		List properties = configuration.configurationsAt(propertiesBase
		                                                 + ".property");
		for (Iterator<HierarchicalConfiguration> it = properties.iterator(); it.hasNext();)
		{
			HierarchicalConfiguration sub = it.next();
			if (propertyName.equals(sub.getString("name")))
			{
				return sub.getString("value");
			}
		}
		return null;
	}
	

	public String getValue(String key)
	{
		return configuration.getProperty(key).toString();
	}
	

	@SuppressWarnings("unchecked")
	public List<String> getValues(String key)
	{
		return configuration.getList(key, new ArrayList<String>());
	}
	

	public static void main(String[] args)
	    throws Exception
	{
		Configuration configuration = new Configuration(new FileInputStream("./connector-config.xml"));
		System.out.println(configuration.getPropertyValue("properties",
		                                                  "services"));
	}
	
}
