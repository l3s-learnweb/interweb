package de.l3s.interwebj.core.query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.Environment;
import de.l3s.interwebj.core.core.ServiceConnector;

public class QueryResultCollector {
    private static final Logger log = LogManager.getLogger(QueryResultCollector.class);

    private Query query;
    private List<QueryResultRetriever> retrievers;

    public QueryResultCollector(Query query) {
        this.query = query;
        this.retrievers = new ArrayList<>();
    }

    public void addQueryResultRetriever(ServiceConnector connector, AuthCredentials authCredentials) {
        retrievers.add(new QueryResultRetriever(connector, authCredentials));
    }

    public QueryResult retrieve() throws InterWebException {
        Cache<Query, QueryResult> cache = Environment.getInstance().getEngine().getCache();

        QueryResult result = cache.getIfPresent(query);
        if (result != null) {
            log.info("Return cached results for: " + query);
            return result;
        }

        log.info("Search for: " + query);

        List<FutureTask<ConnectorResults>> tasks = new ArrayList<FutureTask<ConnectorResults>>();
        for (QueryResultRetriever retriever : retrievers) {
            FutureTask<ConnectorResults> task = new FutureTask<ConnectorResults>(retriever);
            tasks.add(task);
            Thread t = new Thread(task);
            t.start();
        }

        QueryResult results = new QueryResult(query);
        long startTime = System.currentTimeMillis();
        results.setCreatedTime(startTime);
        boolean errorOccurred = false;
        for (FutureTask<ConnectorResults> task : tasks) {
            try {
                results.addQueryResult(task.get(query.getTimeout(), TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                log.error(e);
                throw new InterWebException(e);
            } catch (ExecutionException e) {
                log.error(e);
                throw new InterWebException(e);
            } catch (TimeoutException e) {
                errorOccurred = true;
                task.cancel(true);
                log.error(e);
            }
        }
        results.setElapsedTime(System.currentTimeMillis() - startTime);

        if (!errorOccurred) {
            cache.put(query, results);
        }

        return results;
    }

    private class QueryResultRetriever implements Callable<ConnectorResults> {

        private ServiceConnector connector;
        private AuthCredentials authCredentials;

        QueryResultRetriever(ServiceConnector connector, AuthCredentials authCredentials) {
            this.connector = connector;
            this.authCredentials = authCredentials;
        }

        @Override
        public ConnectorResults call() {
            long startTime = System.currentTimeMillis();
            log.info("[" + connector.getName() + "] Start querying: " + query);

            ConnectorResults queryResult;
            try {
                queryResult = connector.get(query, authCredentials);
            } catch (Throwable e) {
                log.error(e);
                return new ConnectorResults(query, connector.getName());
            }

            long endTime = System.currentTimeMillis();
            log.info("[{}] Finished. [{} of total {}] result(s) found in [{}] ms",
                connector.getName(), queryResult.getResultItems().size(), queryResult.getTotalResultCount(), endTime - startTime);
            return queryResult;
        }
    }
}
