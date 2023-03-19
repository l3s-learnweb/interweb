package de.l3s.interweb.connector.ipernity;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.InterWebException;
import de.l3s.interweb.core.search.SearchResults;
import de.l3s.interweb.core.search.SearchProvider;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.query.QueryFactory;
import de.l3s.interweb.core.search.SearchItem;
import de.l3s.interweb.core.query.SearchRanking;

@Disabled
class IpernityConnectorTest {
    private static final Logger log = LogManager.getLogger(IpernityConnectorTest.class);

    private static final String TEST_KEY = "***REMOVED***";
    private static final String TEST_SECRET = "***REMOVED***";

    private static SearchProvider connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new IpernityConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        Query query = QueryFactory.createQuery("hello world");
        query.addContentType(ContentType.image);
        query.setPerPage(20);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);

        SearchResults queryResult = connector.get(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(20, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }
}
