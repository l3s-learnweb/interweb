package de.l3s.interweb.tomcat.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.search.SearchResponse;
import de.l3s.interweb.core.search.SearchResults;

public class QueryResultCollector {
    private static final Logger log = LogManager.getLogger(QueryResultCollector.class);

    private final Engine engine;
    private final Query query;
    private final List<QueryResultRetriever> retrievers;

    public QueryResultCollector(Engine engine, Query query) {
        this.engine = engine;
        this.query = query;
        this.retrievers = new ArrayList<>();
    }

    public void addQueryResultRetriever(SearchProvider connector, AuthCredentials authCredentials) {
        retrievers.add(new QueryResultRetriever(connector, authCredentials));
    }

    public SearchResponse retrieve() throws InterWebException {
        Cache<Query, SearchResponse> cache = engine.getSearchCache();

        SearchResponse result = cache.getIfPresent(query);
        if (result != null) {
            log.info("Return cached results for: {}", query);
            return result;
        }

        log.info("Search for: {}", query);

        List<FutureTask<SearchResults>> tasks = new ArrayList<>();
        for (QueryResultRetriever retriever : retrievers) {
            FutureTask<SearchResults> task = new FutureTask<>(retriever);
            tasks.add(task);
            Thread t = new Thread(task);
            t.start();
        }

        SearchResponse results = new SearchResponse(query);
        long startTime = System.currentTimeMillis();
        results.setCreatedTime(startTime);
        boolean errorOccurred = false;
        for (FutureTask<SearchResults> task : tasks) {
            try {
                results.addConnectorResults(task.get(query.getTimeout(), TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to execute request", e);
                throw new InterWebException(e);
            } catch (TimeoutException e) {
                errorOccurred = true;
                task.cancel(true);
                log.error("Request timed out", e);
            }
        }
        results.setElapsedTime(System.currentTimeMillis() - startTime);

        if (!errorOccurred) {
            cache.put(query, results);
        }

        return results;
    }

    private class QueryResultRetriever implements Callable<SearchResults> {

        private final SearchProvider connector;
        private final AuthCredentials authCredentials;

        QueryResultRetriever(SearchProvider connector, AuthCredentials authCredentials) {
            this.connector = connector;
            this.authCredentials = authCredentials;
        }

        @Override
        public SearchResults call() {
            long startTime = System.currentTimeMillis();
            log.info("[{}] Start querying: {}", connector.getName(), query);

            SearchResults queryResult;
            try {
                queryResult = connector.get(query, authCredentials);
            } catch (Throwable e) {
                log.error("An error during Connector.get execution", e);
                return new SearchResults(query, connector.getName());
            }

            long endTime = System.currentTimeMillis();
            log.info("[{}] Finished. [{} of total {}] result(s) found in [{}] ms",
                connector.getName(), queryResult.getItems().size(), queryResult.getTotalResults(), endTime - startTime);
            return queryResult;
        }
    }
}
