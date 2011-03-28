package de.l3s.interwebj.config;


import org.apache.commons.configuration.*;


public class Configuration
{
	
	private XMLConfiguration configuration;
	

	public Configuration()
	{
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			configuration = (XMLConfiguration) new XMLConfiguration().interpolatedConfiguration();
			configuration.load(cl.getResourceAsStream("de/l3s/interwebj/config/config.xml"));
		}
		catch (Exception e)
		{
			System.out.println("Unable to load configuration file");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	

	public String getProperty(String key)
	{
		return configuration.getProperty(key).toString();
	}
	
}
