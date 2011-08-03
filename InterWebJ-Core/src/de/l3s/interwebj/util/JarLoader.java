package de.l3s.interwebj.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import de.l3s.interwebj.core.Environment;



public class JarLoader {

	public static void main(String[] args) {

		try {

			// GoogleConnector gc=new GoogleConnector(configuration);

			addURL(new File("lib/google-connector.jar").toURI().toURL());

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addURL(URL u) throws IOException {

		URLClassLoader sysLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		URL urls[] = sysLoader.getURLs();
		for (int i = 0; i < urls.length; i++) {
			
		
			if (urls[i].toString().toLowerCase()
					.equals(u.toString().toLowerCase())) {

				
					Environment.logger.info("URL " + u + " is already in the CLASSPATH");
					
				
				return;
			}
		}
		Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { u });
			Environment.logger.info("URL " + u + " loaded successfully");
			
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException(
					"Error, could not add URL to system classloader");
		}
	}

	public static void addJarFiles(File[] libJarFiles) {

		for (File f : libJarFiles) {
			addJarFile(f);
		}

	}

	public static void addJarFile(File f) {
		try {
			addURL(f.toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
