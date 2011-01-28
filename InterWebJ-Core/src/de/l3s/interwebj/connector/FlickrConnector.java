package de.l3s.interwebj.connector;


import java.util.*;

import de.l3s.interwebj.core.*;
import de.l3s.interwebj.query.*;


public class FlickrConnector
    implements IWServiceConnector
{
	
	@Override
	public QueryResult get(Query query)
	{
		// TODO Auto-generated method stub
		IWEnvironment.getInstance().getLogger().info("requesting flickr");
		return null;
	}
	

	@Override
	public String getName()
	{
		return "flickr";
	}
	

	@Override
	public void put(Object o, List<String> types, Properties properties)
	{
		// TODO Auto-generated method stub
		
	}
	

	public static void main(String[] args)
	{
		
	}
	
}
