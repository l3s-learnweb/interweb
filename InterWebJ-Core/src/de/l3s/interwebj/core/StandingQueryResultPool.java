package de.l3s.interwebj.core;


import java.util.*;
import java.util.concurrent.*;

import de.l3s.interwebj.query.*;


public class StandingQueryResultPool
{
	
	private class RefreshTask
	    extends TimerTask
	{
		
		@Override
		public void run()
		{
			refresh();
		}
	}
	

	private static final long REFRESH_PERIOD = TimeUnit.MILLISECONDS.convert(1,
	                                                                         TimeUnit.MINUTES);
	
	private ConcurrentHashMap<String, QueryResult> standingQueries;
	private Timer timer;
	

	public StandingQueryResultPool()
	{
		standingQueries = new ConcurrentHashMap<String, QueryResult>();
		timer = new Timer();
		timer.schedule(new RefreshTask(), REFRESH_PERIOD, REFRESH_PERIOD);
	}
	

	public QueryResult add(QueryResult queryResult)
	{
		return standingQueries.put(queryResult.getQuery().getId(), queryResult);
	}
	

	public QueryResult get(String id)
	{
		return standingQueries.get(id);
	}
	

	private void refresh()
	{
		long currentTime = System.currentTimeMillis();
		long period = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
		for (String id : standingQueries.keySet())
		{
			QueryResult queryResult = standingQueries.get(id);
			long expirationTime = queryResult.getCreatedTime() + period;
			if (expirationTime <= currentTime)
			{
				standingQueries.remove(id);
			}
		}
	}
}
