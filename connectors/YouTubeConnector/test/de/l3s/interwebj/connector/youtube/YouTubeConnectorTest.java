package de.l3s.interwebj.connector.youtube;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.ConnectorResults;

class YouTubeConnectorTest {
    private static final  String TEST_KEY = "***REMOVED***";
    private static final  String TEST_SECRET = "***REMOVED***";

    private static AbstractServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new YouTubeConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(Query.CT_VIDEO);
        query.setResultCount(5);
        // query.addParam("date_from", "2009-01-01 00:00:00");
        // query.addParam("date_till", "2009-06-01 00:00:00");
        query.setSortOrder(Query.SortOrder.RELEVANCE);

        ConnectorResults queryResult = connector.get(query, null);

        assertEquals(5, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }
}
