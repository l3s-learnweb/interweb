package de.l3s.interwebj.connector.youtube;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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

        ConnectorSearchResults page1 = connector.get(query, null);
        assertEquals(10, page1.getResultItems().size());
        assertTrue(page1.getTotalResultCount() > 100);

        for (ResultItem res : page1.getResultItems()) {
            log.info(res);
        }

        query.setPage(2);
        ConnectorSearchResults page2 = connector.get(query, null);
        for (ResultItem result : page2.getResultItems()) {
            log.info("{}: {}", result.getRank(), result.getTitle());
        }
    }

    @Test
    void parseDate() throws InterWebException {
        ZonedDateTime localDateTime = YouTubeConnector.parseDate("2020-10-04T20:23:33Z");
        assertEquals(ZonedDateTime.of(2020, 10, 4, 20, 23, 33, 0, ZoneId.of("+0")), localDateTime);
    }
}
