package de.l3s.interwebj.connector.bing;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.QueryResult;
import de.l3s.interwebj.core.query.ResultItem;

class BingConnectorTest {
    private static final Logger log = LogManager.getLogger(BingConnectorTest.class);

    private static final  String TEST_KEY = "***REMOVED***";
    private static final  String TEST_SECRET = "";

    private static AbstractServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new BingConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(Query.CT_VIDEO);
        // query.addContentType(Query.CT_IMAGE);
        query.setResultCount(5);
        // query.addParam("date_from", "2009-01-01 00:00:00");
        // query.addParam("date_till", "2009-06-01 00:00:00");
        query.setSortOrder(Query.SortOrder.RELEVANCE);

        QueryResult queryResult = connector.get(query, null);

        for (ResultItem res : queryResult.getResultItems()) {
            log.info("{}: {}", res.getRank(), res.toString());
        }

        assertEquals(20, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }

    @Test
    void getImages() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hannover");
        query.addContentType(Query.CT_IMAGE);
        query.setResultCount(20);
        // query.addParam("date_from", "2009-01-01 00:00:00");
        // query.addParam("date_till", "2009-06-01 00:00:00");
        query.setSortOrder(Query.SortOrder.RELEVANCE);

        QueryResult queryResult = connector.get(query, null);

        for (ResultItem res : queryResult.getResultItems()) {
            log.info("{}: {}", res.getRank(), res.toString());
        }

        assertEquals(20, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() >= 20);
    }
}
