package de.l3s.interwebj.connector.vimeo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.connector.ConnectorSearchResults;
import de.l3s.interwebj.core.connector.ServiceConnector;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.ResultItem;
import de.l3s.interwebj.core.query.SearchExtra;
import de.l3s.interwebj.core.query.SearchRanking;

class VimeoConnectorTest {
    private static final Logger log = LogManager.getLogger(VimeoConnectorTest.class);

    private static final String TEST_KEY = "accesskey";
    private static final String TEST_SECRET = "***REMOVED***";

    private static ServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new VimeoConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        Query query = QueryFactory.createQuery("hello world");
        query.addContentType(ContentType.video);
        query.setPerPage(20);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);

        ConnectorSearchResults queryResult = connector.get(query, null);

        for (ResultItem res : queryResult.getResultItems()) {
            log.info(res);
        }

        assertEquals(20, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }

    @Test
    void getTest() throws InterWebException {
        Query query = QueryFactory.createQuery("mass disaster 1941");
        query.addContentType(ContentType.video);
        query.setLanguage("it");
        query.setExtras(Collections.singleton(SearchExtra.duration));
        query.setPerPage(32);
        query.setPage(2);
        ConnectorSearchResults queryResult = connector.get(query, null);

        for (ResultItem res : queryResult.getResultItems()) {
            log.info(res);
        }

        assertEquals(0, queryResult.getResultItems().size());
    }

    @Test
    void parseDate() throws InterWebException {
        ZonedDateTime localDateTime = VimeoConnector.parseDate("2020-04-21T09:44:08+00:00");
        assertEquals(ZonedDateTime.of(2020, 4, 21, 9, 44, 8, 0, ZoneId.of("+0")), localDateTime);
    }
}
