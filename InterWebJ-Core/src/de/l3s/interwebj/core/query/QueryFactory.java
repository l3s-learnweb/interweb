package de.l3s.interwebj.core.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.l3s.interwebj.core.util.RandomGenerator;

public class QueryFactory
{

    public Query createQuery(String stringQuery)
    {
	return createQuery(stringQuery, new ArrayList<String>());
    }

    public Query createQuery(String stringQuery, List<String> contentTypes)
    {
	return createQuery(stringQuery, contentTypes, new HashMap<String, String>());
    }

    public Query createQuery(String query, List<String> contentTypes, Map<String, String> params)
    {
	RandomGenerator randomGenerator = RandomGenerator.getInstance();
	String id = randomGenerator.nextHexId();
	return new Query(id, query, contentTypes, params);
    }
}
