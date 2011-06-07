package de.l3s.interwebj.query;


import java.util.*;
import java.util.concurrent.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;


public class QueryResultCollector

{
	
	private class QueryResultRetriever
	    implements Callable<QueryResult>
	{
		
		private ServiceConnector connector;
		private AuthCredentials authCredentials;
		

		public QueryResultRetriever(ServiceConnector connector,
		                            AuthCredentials authCredentials)
		{
			this.connector = connector;
			this.authCredentials = authCredentials;
		}
		

		@Override
		public QueryResult call()
		    throws Exception
		{
			long startTime = System.currentTimeMillis();
			Environment.logger.info("[" + connector.getName()
			                        + "] Start querying: " + query);
			QueryResult queryResult = connector.get(query, authCredentials);
			long endTime = System.currentTimeMillis();
			Environment.logger.info("[" + connector.getName() + "] Finished. ["
			                        + queryResult.getResultItems().size()
			                        + " of total "
			                        + queryResult.getTotalResultCount()
			                        + "] result(s) found in ["
			                        + (endTime - startTime) + "] ms");
			return queryResult;
		}
	}
	

	private Query query;
	private QueryResultMerger merger;
	private List<QueryResultRetriever> retrievers;
	

	public QueryResultCollector(Query query, QueryResultMerger merger)
	{
		this.query = query;
		this.merger = merger;
		retrievers = new ArrayList<QueryResultCollector.QueryResultRetriever>();
	}
	

	public void addQueryResultRetriever(ServiceConnector connector,
	                                    AuthCredentials authCredentials)
	{
		retrievers.add(new QueryResultRetriever(connector, authCredentials));
	}
	

	public QueryResult retrieve()
	    throws InterWebException
	{
		List<FutureTask<QueryResult>> tasks = new ArrayList<FutureTask<QueryResult>>();
		for (QueryResultRetriever retriever : retrievers)
		{
			FutureTask<QueryResult> task = new FutureTask<QueryResult>(retriever);
			tasks.add(task);
			Thread t = new Thread(task);
			t.start();
		}
		QueryResult queryResult = new QueryResult(query);
		long startTime = System.currentTimeMillis();
		queryResult.setCreatedTime(startTime);
		for (FutureTask<QueryResult> task : tasks)
		{
			try
			{
				queryResult.addQueryResult(task.get(30, TimeUnit.SECONDS));
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
				throw new InterWebException(e);
			}
			catch (TimeoutException e)
			{
				e.printStackTrace();
				Environment.logger.warn(e);
			}
		}
		queryResult.setElapsedTime(System.currentTimeMillis() - startTime);
		queryResult = merger.merge(queryResult);
		return queryResult;
	}
}
