package de.l3s.interwebj;


import java.io.*;
import java.net.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;


public class Main
{
	
	public static void main(String[] args)
	    throws Exception
	{
		URL configUrl = new File("config" + File.separator + "config.xml").toURI().toURL();
		IWEnvironment iwEnvironment = IWEnvironment.getInstance(configUrl);
		IWEngine iwEngine = iwEnvironment.getEngine();
		iwEngine.get(new Query("test"));
	}
	
}
