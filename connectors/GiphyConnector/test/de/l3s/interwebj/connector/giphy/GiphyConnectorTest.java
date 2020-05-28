package de.l3s.interwebj.connector.giphy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.ResultItem;

class GiphyConnectorTest {
    private static final Logger log = LogManager.getLogger(GiphyConnectorTest.class);

    private static final String TEST_KEY = "apikey";
    private static final String TEST_SECRET = "***REMOVED***";

    private static AbstractServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new GiphyConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(Query.CT_IMAGE);

        ConnectorResults queryResult = connector.get(query, null);

        for (ResultItem res : queryResult.getResultItems()) {
            log.info("{}: {}", res.getRank(), res.toString());
        }

        assertEquals(10, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }
}
