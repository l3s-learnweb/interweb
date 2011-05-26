package de.l3s.interwebj.core;


import java.util.*;
import java.util.concurrent.*;

import de.l3s.interwebj.query.*;
import de.l3s.interwebj.util.*;


public class StandingQueryResultPool
{
	
	private Map<String, QueryResult> standingQueries;
	

	public StandingQueryResultPool()
	{
		ExpirationPolicy.Builder builder = new ExpirationPolicy.Builder();
		ExpirationPolicy expirationPolicy = builder.timeToLive(10,
		                                                       TimeUnit.MINUTES).build();
		standingQueries = new ExpirableMap<String, QueryResult>(expirationPolicy);
	}
	

	public QueryResult add(QueryResult queryResult)
	{
		return standingQueries.put(queryResult.getQuery().getId(), queryResult);
	}
	

	public QueryResult get(String id)
	{
		return standingQueries.get(id);
	}
}
