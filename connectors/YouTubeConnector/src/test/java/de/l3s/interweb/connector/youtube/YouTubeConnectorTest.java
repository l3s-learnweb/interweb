package de.l3s.interweb.connector.youtube;

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
import de.l3s.interweb.core.connector.ConnectorSearchResults;
import de.l3s.interweb.core.connector.ServiceConnector;
import de.l3s.interweb.core.query.ContentType;
import de.l3s.interweb.core.query.Query;
import de.l3s.interweb.core.query.QueryFactory;
import de.l3s.interweb.core.query.ResultItem;
import de.l3s.interweb.core.query.SearchExtra;
import de.l3s.interweb.core.query.SearchRanking;

@Disabled
class YouTubeConnectorTest {
    private static final Logger log = LogManager.getLogger(YouTubeConnectorTest.class);

    private static final String TEST_KEY = "***REMOVED***";
    private static final String TEST_SECRET = "***REMOVED***";

    private static ServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new YouTubeConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        Query query = QueryFactory.createQuery("spacex");
        query.addContentType(ContentType.video);
        query.setPerPage(10);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);
        // query.addSearchExtra(SearchExtra.statistics);
        query.addSearchExtra(SearchExtra.duration);
        query.addSearchExtra(SearchExtra.tags);

        for (int i = 1; i < 4; ++i) {
            query.setPage(i);
            ConnectorSearchResults page = connector.get(query, null);

            assertEquals(10, page.getResultItems().size());
            assertTrue(page.getTotalResultCount() > 100);

            for (ResultItem result : page.getResultItems()) {
                log.info("{}: {} {}", result.getRank(), result.getTitle(), result.getUrl());
            }
        }
    }

    @Test
    void parseDate() throws InterWebException {
        ZonedDateTime localDateTime = YouTubeConnector.parseDate("2020-10-04T20:23:33Z");
        assertEquals(ZonedDateTime.of(2020, 10, 4, 20, 23, 33, 0, ZoneId.of("+0")), localDateTime);
    }
}
