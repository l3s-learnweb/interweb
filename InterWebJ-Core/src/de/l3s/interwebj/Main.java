package de.l3s.interwebj;


import java.io.*;
import java.net.*;

import de.l3s.interwebj.core.*;


public class Main
{
	
	public static void main(String[] args)
	    throws Exception
	{
		URL configUrl = new File("config" + File.separator + "config.xml").toURI().toURL();
		Environment iwEnvironment = Environment.getInstance(configUrl);
		Engine iwEngine = iwEnvironment.getEngine();
	}
	
}
