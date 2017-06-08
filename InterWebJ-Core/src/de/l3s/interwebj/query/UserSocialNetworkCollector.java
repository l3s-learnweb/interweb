package de.l3s.interwebj.query;

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

	    Environment.logger.info("[" + connector.getName() + "] Start querying: " + userid);
	    UserSocialNetworkResult queryResult = connector.getUserSocialNetwork(userid, authCredentials);
	    //queryResult = Environment.getInstance().getPrivacyClassifier().classify(queryResult);

	    return queryResult;
	}
    }

    private String userid;
    private List<UserResultRetriever> retrievers;

    public UserSocialNetworkCollector(String userid, QueryResultMerger merger)
    {
	this.userid = userid;
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
