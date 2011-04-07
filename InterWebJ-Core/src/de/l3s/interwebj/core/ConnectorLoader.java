package de.l3s.interwebj.core;


import java.io.*;
import java.util.*;

import org.apache.commons.configuration.*;
import org.xeustechnologies.jcl.*;

import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.connector.*;


public class ConnectorLoader
{
	
	private static final String CONNECTOR_CONFIG_FILE_NAME = "connector-config.xml";
	

	private File[] getJars(File dir)
	{
		return dir.listFiles(new FileFilter()
		{
			
			@Override
			public boolean accept(File file)
			{
				return !file.isDirectory() && file.getName().endsWith(".jar");
			}
		});
	}
	

	public List<ServiceConnector> load(String pluginDirPath)
	{
		List<ServiceConnector> connectors = new ArrayList<ServiceConnector>();
		File pluginDir = new File(pluginDirPath);
		Environment.logger.info("loading plugins from directory: ["
		                        + pluginDir.getAbsolutePath() + "]");
		File[] connectorDirs = pluginDir.listFiles(new FileFilter()
		{
			
			@Override
			public boolean accept(File file)
			{
				return file.isDirectory() && !file.getName().startsWith(".");
			}
		});
		if (connectorDirs != null)
		{
			
			for (File connectorDir : connectorDirs)
			{
				ServiceConnector connector = loadConnector(connectorDir);
				if (connector != null)
				{
					connectors.add(connector);
				}
			}
		}
		else
		{
			Environment.logger.error("[" + pluginDirPath
			                         + "] is not a directory or doesn't exist");
		}
		return connectors;
	}
	

	private ServiceConnector loadConnector(File connectorDir)
	{
		ServiceConnector connector = null;
		try
		{
			Environment.logger.info("trying load connector from folder: ["
			                        + connectorDir.getAbsolutePath() + "]");
			File configFile = new File(connectorDir, CONNECTOR_CONFIG_FILE_NAME);
			Configuration configuration;
			configuration = new Configuration(new FileInputStream(configFile));
			String connectorName = configuration.getProperty("name");
			File connectorJarFile = new File(connectorDir,
			                                 configuration.getProperty("jar-name"));
			JarClassLoader jcl = new JarClassLoader();
			try
			{
				jcl.add(new FileInputStream(connectorJarFile));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				Environment.logger.error("No jar file found for connector ["
				                         + connectorName
				                         + "]. Check configuration file");
				return null;
			}
			Environment.logger.info("connector jar file: ["
			                        + connectorJarFile.getAbsolutePath()
			                        + "] successfully loaded");
			File libDir = new File(connectorDir, "lib");
			File[] libJarFiles = getJars(libDir);
			if (libJarFiles != null)
			{
				for (File libJarFile : libJarFiles)
				{
					jcl.add(new FileInputStream(libJarFile));
					Environment.logger.info("dependency library jar file: ["
					                        + libJarFile.getAbsolutePath()
					                        + "] successfully loaded");
				}
			}
			JclObjectFactory factory = JclObjectFactory.getInstance();
			String connectorClassName = configuration.getProperty("class");
			Object o = factory.create(jcl, connectorClassName, configuration);
			connector = JclUtils.cast(o, ServiceConnector.class);
			Environment.logger.info("Connector [" + connector.getName()
			                        + "] successfully loaded");
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return connector;
	}
}
