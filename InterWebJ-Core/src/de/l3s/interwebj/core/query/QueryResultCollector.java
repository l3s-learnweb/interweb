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
    private QueryResultMerger merger;
    private List<QueryResultRetriever> retrievers;

    public QueryResultCollector(Query query, QueryResultMerger merger) {
        this.query = query;
        this.merger = merger;
        retrievers = new ArrayList<QueryResultCollector.QueryResultRetriever>();
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

        List<FutureTask<QueryResult>> tasks = new ArrayList<FutureTask<QueryResult>>();
        for (QueryResultRetriever retriever : retrievers) {
            FutureTask<QueryResult> task = new FutureTask<QueryResult>(retriever);
            tasks.add(task);
            Thread t = new Thread(task);
            t.start();
        }

        QueryResult queryResult = new QueryResult(query);
        long startTime = System.currentTimeMillis();
        queryResult.setCreatedTime(startTime);
        boolean errorOccurred = false;
        for (FutureTask<QueryResult> task : tasks) {
            try {
                queryResult.addQueryResult(task.get(query.getTimeout(), TimeUnit.SECONDS));
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
        queryResult.setElapsedTime(System.currentTimeMillis() - startTime);
        queryResult = merger.merge(queryResult);

        if (!errorOccurred) {
            cache.put(query, queryResult);
        }

        return queryResult;
    }

    private class QueryResultRetriever implements Callable<QueryResult> {

        private ServiceConnector connector;
        private AuthCredentials authCredentials;

        QueryResultRetriever(ServiceConnector connector, AuthCredentials authCredentials) {
            this.connector = connector;
            this.authCredentials = authCredentials;
        }

        @Override
        public QueryResult call() {
            long startTime = System.currentTimeMillis();
            log.info("[" + connector.getName() + "] Start querying: " + query);

            QueryResult queryResult;
            try {
                queryResult = connector.get(query, authCredentials);
            } catch (Throwable e) {
                log.error(e);
                return new QueryResult(query);
            }

            long endTime = System.currentTimeMillis();
            log.info("[{}] Finished. [{} of total {}] result(s) found in [{}] ms",
                connector.getName(), queryResult.getResultItems().size(), queryResult.getTotalResultCount(), endTime - startTime);
            return queryResult;
        }
    }
}
