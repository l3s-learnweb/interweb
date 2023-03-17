package de.l3s.interweb.tomcat.core;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.query.QueryFactory;
import de.l3s.interweb.core.query.SearchRanking;
import de.l3s.interweb.core.query.SearchResults;
import de.l3s.interweb.core.query.SearchScope;
import de.l3s.interweb.tomcat.db.Database;

class EngineTest {
    private static final Logger log = LogManager.getLogger(EngineTest.class);

    @Test
    void getConnectorAuthCredentials() throws InterWebException {
        Database database = Environment.getInstance().getDatabase();
        InterWebPrincipal principal;
        principal = database.authenticate("olex", "123456");
        AuthCredentials authCredentials;
        authCredentials = database.readConnectorAuthCredentials("flickr");
        log.info(authCredentials);
        Engine engine = new Engine(database);
        engine.loadConnectors();

        List<String> connectorNames = engine.getConnectorNames();
        log.info("Searching in connectors: {}", connectorNames);
        int retryCount = 5;
        for (int i = 0; i < retryCount; i++) {
            testSearch("london", connectorNames, engine, principal);
        }

        log.info("finished");
    }

    private static void testSearch(String word, List<String> connectorNames, Engine engine, InterWebPrincipal principal) throws InterWebException {
        Query query = QueryFactory.createQuery(word);
        query.addContentType(ContentType.video);
        query.addContentType(ContentType.image);
        query.setSearchScope(SearchScope.text);
        query.setPerPage(10);
        query.setRanking(SearchRanking.relevance);
        for (String connectorName : connectorNames) {
            query.addConnectorName(connectorName);
        }
        QueryResultCollector collector = engine.getQueryResultCollector(query, principal);

        SearchResults searchResults = collector.retrieve();
        log.info("query: [{}]", query);
        log.info("elapsed time : [{}]", searchResults.getElapsedTime());
    }
}
