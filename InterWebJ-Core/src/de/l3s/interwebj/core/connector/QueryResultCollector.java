package de.l3s.interwebj.core.connector;

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
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.SearchResults;

public class QueryResultCollector {
    private static final Logger log = LogManager.getLogger(QueryResultCollector.class);

    private final Query query;
    private final List<QueryResultRetriever> retrievers;

    public QueryResultCollector(Query query) {
        this.query = query;
        this.retrievers = new ArrayList<>();
    }

    public void addQueryResultRetriever(ServiceConnector connector, AuthCredentials authCredentials) {
        retrievers.add(new QueryResultRetriever(connector, authCredentials));
    }

    public SearchResults retrieve() throws InterWebException {
        Cache<Query, SearchResults> cache = Environment.getInstance().getEngine().getSearchCache();

        SearchResults result = cache.getIfPresent(query);
        if (result != null) {
            log.info("Return cached results for: {}", query);
            return result;
        }

        log.info("Search for: {}", query);

        List<FutureTask<ConnectorSearchResults>> tasks = new ArrayList<>();
        for (QueryResultRetriever retriever : retrievers) {
            FutureTask<ConnectorSearchResults> task = new FutureTask<>(retriever);
            tasks.add(task);
            Thread t = new Thread(task);
            t.start();
        }

        SearchResults results = new SearchResults(query);
        long startTime = System.currentTimeMillis();
        results.setCreatedTime(startTime);
        boolean errorOccurred = false;
        for (FutureTask<ConnectorSearchResults> task : tasks) {
            try {
                results.addConnectorResults(task.get(query.getTimeout(), TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException e) {
                log.catching(e);
                throw new InterWebException(e);
            } catch (TimeoutException e) {
                errorOccurred = true;
                task.cancel(true);
                log.catching(e);
            }
        }
        results.setElapsedTime(System.currentTimeMillis() - startTime);

        if (!errorOccurred) {
            cache.put(query, results);
        }

        return results;
    }

    private class QueryResultRetriever implements Callable<ConnectorSearchResults> {

        private final ServiceConnector connector;
        private final AuthCredentials authCredentials;

        QueryResultRetriever(ServiceConnector connector, AuthCredentials authCredentials) {
            this.connector = connector;
            this.authCredentials = authCredentials;
        }

        @Override
        public ConnectorSearchResults call() {
            long startTime = System.currentTimeMillis();
            log.info("[{}] Start querying: {}", connector.getName(), query);

            ConnectorSearchResults queryResult;
            try {
                queryResult = connector.get(query, authCredentials);
            } catch (Throwable e) {
                log.error("An error during Connector.get execution", e);
                return new ConnectorSearchResults(query, connector.getName());
            }

            long endTime = System.currentTimeMillis();
            log.info("[{}] Finished. [{} of total {}] result(s) found in [{}] ms",
                connector.getName(), queryResult.getResultItems().size(), queryResult.getTotalResultCount(), endTime - startTime);
            return queryResult;
        }
    }
}
