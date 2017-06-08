package de.l3s.interwebj.socialsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.Environment;
import de.l3s.interwebj.core.ServiceConnector;

public class SocialSearchResultCollector

{

    private class SocialSearchResultRetriever implements Callable<SocialSearchResult>
    {

	private ServiceConnector connector;
	private AuthCredentials authCredentials;

	public SocialSearchResultRetriever(ServiceConnector connector, AuthCredentials authCredentials)
	{
	    this.connector = connector;
	    this.authCredentials = authCredentials;
	}

	@Override
	public SocialSearchResult call() throws Exception
	{
	    long startTime = System.currentTimeMillis();
	    Environment.logger.info("[" + connector.getName() + "] Start querying: " + query);
	    SocialSearchResult SocialSearchResult = connector.get(query, authCredentials);
	    //SocialSearchResult = Environment.getInstance().getPrivacyClassifier().classify(SocialSearchResult);
	    long endTime = System.currentTimeMillis();
	    Environment.logger.info("[" + connector.getName() + "] Finished. [" + SocialSearchResult.getResultItems().size() + " of total " + SocialSearchResult.getTotalResultCount() + "] result(s) found in [" + (endTime - startTime) + "] ms");
	    return SocialSearchResult;
	}
    }

    private SocialSearchQuery query;

    private List<SocialSearchResultRetriever> retrievers;

    public SocialSearchResultCollector(SocialSearchQuery query)
    {
	this.query = query;

	retrievers = new ArrayList<SocialSearchResultCollector.SocialSearchResultRetriever>();
    }

    public SocialSearchResultCollector(String query2)
    {
	// TODO Auto-generated constructor stub
    }

    public void addSocialSearchResultRetriever(ServiceConnector connector, AuthCredentials authCredentials)
    {
	retrievers.add(new SocialSearchResultRetriever(connector, authCredentials));
    }

    public SocialSearchResult retrieve() throws InterWebException
    {
	List<FutureTask<SocialSearchResult>> tasks = new ArrayList<FutureTask<SocialSearchResult>>();
	for(SocialSearchResultRetriever retriever : retrievers)
	{
	    FutureTask<SocialSearchResult> task = new FutureTask<SocialSearchResult>(retriever);
	    tasks.add(task);
	    Thread t = new Thread(task);
	    t.start();
	}
	SocialSearchResult result = new SocialSearchResult(query);
	long startTime = System.currentTimeMillis();
	result.setCreatedTime(startTime);
	for(FutureTask<SocialSearchResult> task : tasks)
	{
	    try
	    {
		result.addResult(task.get(30, TimeUnit.SECONDS));
	    }
	    catch(InterruptedException e)
	    {
		e.printStackTrace();
		throw new InterWebException(e);
	    }
	    catch(ExecutionException e)
	    {
		e.printStackTrace();
		throw new InterWebException(e);
	    }
	    catch(TimeoutException e)
	    {
		task.cancel(true);
		e.printStackTrace();
		Environment.logger.severe(e.getMessage());
	    }
	}
	result.setElapsedTime(System.currentTimeMillis() - startTime);
	//SocialSearchResult = merger.merge(SocialSearchResult);
	return result;
    }
}
