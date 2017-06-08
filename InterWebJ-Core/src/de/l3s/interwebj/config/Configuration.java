package de.l3s.interwebj.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class Configuration
{

    private XMLConfiguration configuration;

    public Configuration(InputStream is)
    {
	configuration = (XMLConfiguration) new XMLConfiguration().interpolatedConfiguration();
	try
	{
	    configuration.load(is);
	}
	catch(ConfigurationException e)
	{
	    e.printStackTrace();
	}
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public String getPropertyValue(String propertiesBase, String propertyName)
    {
	List properties = configuration.configurationsAt(propertiesBase + ".property");
	for(Iterator<HierarchicalConfiguration> it = properties.iterator(); it.hasNext();)
	{
	    HierarchicalConfiguration sub = it.next();
	    if(propertyName.equals(sub.getString("name")))
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
}
