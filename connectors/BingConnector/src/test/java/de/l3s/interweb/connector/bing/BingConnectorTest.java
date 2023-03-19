package de.l3s.interweb.connector.bing;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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

@Disabled
class BingConnectorTest {
    private static final Logger log = LogManager.getLogger(BingConnectorTest.class);

    private static final String TEST_KEY = "accesskey";
    private static final String TEST_SECRET = "***REMOVED***";

    private static SearchProvider connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new BingConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        Query query = QueryFactory.createQuery("hello world");
        query.setPerPage(30);
        query.addContentType(ContentType.text);
        query.setDateFrom("2009-01-01 00:00:00");
        query.setDateTill("2010-06-01 00:00:00");

        SearchResults queryResult = connector.get(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res.toString());
        }

        assertEquals(30, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void getImages() throws InterWebException {
        Query query = QueryFactory.createQuery("hannover");
        query.addContentType(ContentType.image);
        query.setPerPage(30);
        query.setPage(2);

        SearchResults queryResult = connector.get(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(30, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void getVideos() throws InterWebException {
        Query query = QueryFactory.createQuery("hannover");
        query.addContentType(ContentType.video);
        query.setPerPage(30);
        query.setPage(2);

        SearchResults queryResult = connector.get(query, null);

        for (SearchItem res : queryResult.getItems()) {
            log.info(res.toString());
        }

        assertEquals(30, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void parseDate() throws InterWebException {
        ZonedDateTime date0 = BingConnector.parseDate("2020-01-28T17:18:45");
        ZonedDateTime date1 = BingConnector.parseDate("2020-01-28T17:18:45.0000000");
        ZonedDateTime date2 = BingConnector.parseDate("2019-11-12T12:37:00.0000000Z");
        ZonedDateTime date3 = BingConnector.parseDate("2020-11-28T23:17:00.0000000Z");

        assertEquals(ZonedDateTime.of(2020, 1, 28, 17, 18, 45, 0, ZoneId.systemDefault()), date0);
        assertEquals(ZonedDateTime.of(2020, 1, 28, 17, 18, 45, 0, ZoneId.systemDefault()), date1);
        assertEquals(ZonedDateTime.of(2019, 11, 12, 12, 37, 0, 0, ZoneId.systemDefault()), date2);
        assertEquals(ZonedDateTime.of(2020, 11, 28, 23, 17, 0, 0, ZoneId.systemDefault()), date3);
    }
}
