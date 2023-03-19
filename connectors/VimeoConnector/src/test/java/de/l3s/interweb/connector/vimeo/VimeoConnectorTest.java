package de.l3s.interweb.connector.vimeo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

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
import de.l3s.interweb.core.query.SearchExtra;
import de.l3s.interweb.core.query.SearchRanking;

@Disabled
class VimeoConnectorTest {
    private static final Logger log = LogManager.getLogger(VimeoConnectorTest.class);

    private static final String TEST_KEY = "accesskey";
    private static final String TEST_SECRET = "***REMOVED***";

    private static SearchProvider connector;

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

        SearchResults queryResult = connector.get(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(20, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void getTest() throws InterWebException {
        Query query = QueryFactory.createQuery("mass disaster 1941");
        query.addContentType(ContentType.video);
        query.setLanguage("it");
        query.setExtras(Collections.singleton(SearchExtra.duration));
        query.setPerPage(32);
        query.setPage(2);
        SearchResults queryResult = connector.get(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(0, queryResult.getItems().size());
    }

    @Test
    void parseDate() throws InterWebException {
        ZonedDateTime localDateTime = VimeoConnector.parseDate("2020-04-21T09:44:08+00:00");
        assertEquals(ZonedDateTime.of(2020, 4, 21, 9, 44, 8, 0, ZoneId.of("+0")), localDateTime);
    }
}
