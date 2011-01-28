package de.l3s.interwebj.core;


import java.util.*;

import de.l3s.interwebj.connector.*;
import de.l3s.interwebj.query.*;


public class IWEngine
{
	
	private Map<String, IWServiceConnector> connectorMap;
	

	public IWEngine(IWConfiguration configuration)
	{
		init(configuration);
	}
	

	public QueryResult get(final Query query)
	{
		for (String name : connectorMap.keySet())
		{
			final IWServiceConnector connector = connectorMap.get(name);
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					connector.get(query);
				}
			}).start();
		}
		IWEnvironment.logger.info("done");
		return null;
	}
	

	private void init(IWConfiguration configuration)
	{
		connectorMap = new HashMap<String, IWServiceConnector>();
		IWServiceConnector connector = new FlickrConnector();
		connectorMap.put(connector.getName(), connector);
	}
	
}
