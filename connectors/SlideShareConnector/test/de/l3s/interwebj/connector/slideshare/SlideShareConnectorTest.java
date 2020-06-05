package de.l3s.interwebj.connector.slideshare;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.ServiceConnector;
import de.l3s.interwebj.core.query.ConnectorResults;
import de.l3s.interwebj.core.query.ContentType;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.SearchRanking;
import de.l3s.interwebj.core.query.SearchScope;

class SlideShareConnectorTest {
    private static final String TEST_KEY = "***REMOVED***";
    private static final String TEST_SECRET = "***REMOVED***";

    private static ServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new SlideShareConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(ContentType.video);
        query.addContentType(ContentType.image);
        query.addContentType(ContentType.text);
        query.addContentType(ContentType.presentation);
        query.addContentType(ContentType.audio);
        query.addSearchScope(SearchScope.text);
        query.addSearchScope(SearchScope.tags);
        query.setPerPage(5);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTill("2009-06-01 00:00:00");
        query.setRanking(SearchRanking.relevance);

        ConnectorResults queryResult = connector.get(query, null);

        assertEquals(16, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);

        String embedded = connector.getEmbedded(null, "https://www.slideshare.net/pacific2000/flowers-presentation-715934", 240, 240);
        System.out.println(embedded);
    }
}
