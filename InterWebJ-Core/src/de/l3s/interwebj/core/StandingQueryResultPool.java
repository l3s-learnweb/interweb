package de.l3s.interwebj.core;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.l3s.interwebj.query.QueryResult;
import de.l3s.interwebj.util.ExpirableMap;
import de.l3s.interwebj.util.ExpirationPolicy;

public class StandingQueryResultPool
{

    private Map<String, QueryResult> standingQueries;

    public StandingQueryResultPool()
    {
	ExpirationPolicy.Builder builder = new ExpirationPolicy.Builder();
	ExpirationPolicy expirationPolicy = builder.timeToLive(10, TimeUnit.MINUTES).build();
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
