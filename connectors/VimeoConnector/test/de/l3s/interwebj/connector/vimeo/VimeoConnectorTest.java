package de.l3s.interwebj.connector.vimeo;

import de.l3s.interwebj.AuthCredentials;
import de.l3s.interwebj.InterWebException;
import de.l3s.interwebj.core.AbstractServiceConnector;
import de.l3s.interwebj.query.Query;
import de.l3s.interwebj.query.QueryFactory;
import de.l3s.interwebj.query.QueryResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VimeoConnectorTest {
    private final static String TEST_KEY = "***REMOVED***";
    private final static String TEST_SECRET = "***REMOVED***";

    private static AbstractServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new VimeoConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(Query.CT_VIDEO);
        query.setResultCount(5);
        //		query.addParam("date_from", "2009-01-01 00:00:00");
        //		query.addParam("date_till", "2009-06-01 00:00:00");
        query.setSortOrder(Query.SortOrder.RELEVANCE);

        QueryResult queryResult = connector.get(query, null);

        assertEquals(5, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }
}