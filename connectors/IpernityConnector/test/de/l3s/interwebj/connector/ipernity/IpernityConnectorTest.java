package de.l3s.interwebj.connector.ipernity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.ConnectorSearchResults;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.SearchRanking;

class IpernityConnectorTest {
    private static final String TEST_KEY = "***REMOVED***";
    private static final String TEST_SECRET = "***REMOVED***";

    private static ServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new IpernityConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(ContentType.image);
        query.setPerPage(20);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);

        ConnectorSearchResults queryResult = connector.get(query, null);

        assertEquals(20, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }
}
