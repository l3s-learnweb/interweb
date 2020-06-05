package de.l3s.interwebj.connector.giphy;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.ResultItem;

class GiphyConnectorTest {
    private static final Logger log = LogManager.getLogger(GiphyConnectorTest.class);

    private static final String TEST_KEY = "accesskey";
    private static final String TEST_SECRET = "***REMOVED***";

    private static ServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new GiphyConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(ContentType.image);

        ConnectorResults queryResult = connector.get(query, null);

        for (ResultItem res : queryResult.getResultItems()) {
            log.info(res);
        }

        assertEquals(10, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }
}
