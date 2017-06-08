package de.l3s.interwebj.query;

import java.util.*;
import java.util.concurrent.*;

import de.l3s.interwebj.*;
import de.l3s.interwebj.core.*;

public class UserSocialNetworkCollector

{

    private class UserResultRetriever implements Callable<UserSocialNetworkResult>
    {

	private ServiceConnector connector;
	private AuthCredentials authCredentials;

	public UserResultRetriever(ServiceConnector connector, AuthCredentials authCredentials)
	{
	    this.connector = connector;
	    this.authCredentials = authCredentials;
	}

	public UserSocialNetworkResult call() throws Exception
	{
	    long startTime = System.currentTimeMillis();
	    Environment.logger.info("[" + connector.getName() + "] Start querying: " + userid);
	    UserSocialNetworkResult queryResult = connector.getUserSocialNetwork(userid, authCredentials);
	    //queryResult = Environment.getInstance().getPrivacyClassifier().classify(queryResult);
	    long endTime = System.currentTimeMillis();
	    /*Environment.logger.info("[" + connector.getName() + "] Finished. ["
	                            + queryResult.getResultItems().size()
	                            + " of total "
	                            + queryResult.getTotalResultCount()
	                            + "] result(s) found in ["
	                            + (endTime - startTime) + "] ms");*/
	    return queryResult;
	}
    }

    private String userid;
    private QueryResultMerger merger;
    private List<UserResultRetriever> retrievers;

    public UserSocialNetworkCollector(String userid, QueryResultMerger merger)
    {
	this.userid = userid;
	this.merger = merger;
	retrievers = new ArrayList<UserSocialNetworkCollector.UserResultRetriever>();
    }

    public void addSocialNetworkRetriever(ServiceConnector connector, AuthCredentials authCredentials)
    {
	retrievers.add(new UserResultRetriever(connector, authCredentials));
    }

    public UserSocialNetworkResult retrieve() throws InterWebException
    {
	List<FutureTask<UserSocialNetworkResult>> tasks = new ArrayList<FutureTask<UserSocialNetworkResult>>();
	for(UserResultRetriever retriever : retrievers)
	{
	    FutureTask<UserSocialNetworkResult> task = new FutureTask<UserSocialNetworkResult>(retriever);
	    tasks.add(task);
	    Thread t = new Thread(task);
	    t.start();
	}
	UserSocialNetworkResult userSNResult = new UserSocialNetworkResult(userid);
	long startTime = System.currentTimeMillis();
	userSNResult.setCreatedTime(startTime);
	for(FutureTask<UserSocialNetworkResult> task : tasks)
	{
	    try
	    {
		userSNResult.addSocialNetworkResult(task.get(30, TimeUnit.SECONDS));
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
	userSNResult.setElapsedTime(System.currentTimeMillis() - startTime);
	//queryResult = merger.merge(queryResult);
	return userSNResult;
    }
}
