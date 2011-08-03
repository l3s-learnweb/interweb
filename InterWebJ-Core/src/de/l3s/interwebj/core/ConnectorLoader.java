package de.l3s.interwebj.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.JclUtils;

import de.l3s.interwebj.config.Configuration;
import de.l3s.interwebj.util.JarLoader;



public class ConnectorLoader {

	public static final String CONNECTOR_CONFIG_FILE_NAME = "connector-config.xml";

	public List<ServiceConnector> load(String pluginDirPath) {
		List<ServiceConnector> connectors = new ArrayList<ServiceConnector>();
		File pluginDir = new File(pluginDirPath);
		Environment.logger.info("loading plugins from directory: ["
				+ pluginDir.getAbsolutePath() + "]");
		File[] connectorDirs = pluginDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory() && !file.getName().startsWith(".");
			}
		});
		if (connectorDirs != null) {

			for (File connectorDir : connectorDirs) {
				ServiceConnector connector = loadConnector(connectorDir);
				if (connector != null) {
					connectors.add(connector);
				}
			}
		} else {
			Environment.logger.severe("[" + pluginDirPath
					+ "] is not a directory or doesn't exist");
		}
		return connectors;
	}

	private File[] getJars(File dir) {
		return dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return !file.isDirectory() && file.getName().endsWith(".jar");
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public ServiceConnector loadLinkedConnector(String realPath, String name) {

		// String connectorName=name+"Controller";
		Class cl;
		File connectorDir = new File(new File(new File(realPath, "WEB-INF"),
				"connectors"), name.toLowerCase());
		try {


			Environment.logger.info("trying load connector from folder: ["
					+ connectorDir.getAbsolutePath() + "]");
			File configFile = new File(connectorDir, CONNECTOR_CONFIG_FILE_NAME);
			Configuration configuration = new Configuration(
					new FileInputStream(configFile));
			String connectorName = configuration.getValue("class");

			
			Environment.logger.info("connector jar file: [" + connectorName
					+ "] successfully loaded");

			File libDir = new File(connectorDir, "lib");

			File[] libJarFiles = getJars(libDir);
			if(libDir.exists()){
				JarLoader.addJarFiles(libJarFiles);
			}

			cl = Class.forName(connectorName);
			@SuppressWarnings("unchecked")
			java.lang.reflect.Constructor co = cl.getConstructor(Configuration.class);			
		
			Object ins = co.newInstance(configuration);

			return (ServiceConnector) ins;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException();
		// continue;

	}

	private ServiceConnector loadConnector(File connectorDir) {
		ServiceConnector connector = null;
		try {
			Environment.logger.info("trying load connector from folder: ["
					+ connectorDir.getAbsolutePath() + "]");
			File configFile = new File(connectorDir, CONNECTOR_CONFIG_FILE_NAME);
			Configuration configuration = new Configuration(
					new FileInputStream(configFile));
			String connectorName = configuration.getValue("name");
			File connectorJarFile = new File(connectorDir,
					configuration.getValue("jar-name"));
			JarClassLoader jcl = new JarClassLoader();
			try {
				jcl.add(new FileInputStream(connectorJarFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Environment.logger.severe("No jar file found for connector ["
						+ connectorName + "]. Check configuration file");
				return null;
			}
			Environment.logger.info("connector jar file: ["
					+ connectorJarFile.getAbsolutePath()
					+ "] successfully loaded");
			File libDir = new File(connectorDir, "lib");
			File[] libJarFiles = getJars(libDir);
			if (libJarFiles != null) {
				for (File libJarFile : libJarFiles) {
					jcl.add(new FileInputStream(libJarFile));
					Environment.logger.info("dependency library jar file: ["
							+ libJarFile.getAbsolutePath()
							+ "] successfully loaded");
				}
			}
			JclObjectFactory factory = JclObjectFactory.getInstance();
			String connectorClassName = configuration.getValue("class");
			Object o = factory.create(jcl, connectorClassName, configuration);
			connector = JclUtils.cast(o, ServiceConnector.class);
			Environment.logger.info("Connector [" + connector.getName()
					+ "] successfully loaded");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Environment.logger.severe(e.getMessage());
		}
		return connector;
	}
}
