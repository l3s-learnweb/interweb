package de.l3s.interweb.connector.ipernity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.describe.DescribeQuery;
import de.l3s.interweb.core.describe.DescribeResults;
import de.l3s.interweb.core.search.*;

@Disabled
@QuarkusTest
class IpernityConnectorTest {
    private static final Logger log = Logger.getLogger(IpernityConnectorTest.class);

    @Inject
    IpernityConnector connector;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("tree");
        query.setContentTypes(ContentType.image);
        query.setPerPage(20);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTo("2009-06-01 00:00:00");
        query.setSort(SearchSort.relevance);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(20, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void findIds() {
        assertEquals("52123206", connector.findId("http://www.ipernity.com/doc/homaris/52123206"));
        assertEquals("52101566", connector.findId("http://www.ipernity.com/doc/pics-um/52101566/in/group/26250"));
        assertEquals("51888924", connector.findId("http://www.ipernity.com/doc/pics-um/51888924/in/album/1345672"));
    }

    @Test
    void describe() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("50253056");

        DescribeResults queryResult = connector.describe(query).await().indefinitely();

        assertEquals("50253056", queryResult.getEntity().getId());
        assertEquals("http://ipernity.com/doc/2317344/50253056", queryResult.getEntity().getUrl());
        assertEquals(ContentType.image, queryResult.getEntity().getType());
        assertEquals("Am Blejsko jezero (PiP)", queryResult.getEntity().getTitle());
        assertEquals("Blick auf den Bleder See (Blejsko jezero) mit der Insel Blejski Otok und der Marienkirche (Cerkev Marijinega vnebovzetja). Die Kirche erreicht man, nach einer Bootsfahrt, über 99 Steinstufen.", queryResult.getEntity()
            .getDescription());
        assertEquals(Instant.ofEpochMilli(1594306896000L), queryResult.getEntity().getDate());
        assertEquals("Pics-UM", queryResult.getEntity().getAuthor());
        assertEquals("http://ipernity.com/home/2317344", queryResult.getEntity().getAuthorUrl());
        assertTrue(queryResult.getEntity().getViewsCount() > 450);
        assertTrue(queryResult.getEntity().getCommentsCount() > 30);
        assertEquals(new Thumbnail("http://cdn.ipernity.com/200/30/56/50253056.1fa7020b.240.jpg?r2", 240, 161), queryResult.getEntity().getThumbnailSmall());
        assertEquals(new Thumbnail("http://cdn.ipernity.com/200/30/56/50253056.1fa7020b.640.jpg?r2", 640, 427), queryResult.getEntity().getThumbnailMedium());
        assertEquals(new Thumbnail("http://cdn.ipernity.com/200/30/56/50253056.24df04e2.1024.jpg?r2", 1024, 683), queryResult.getEntity().getThumbnailLarge());
        assertEquals(new Thumbnail("http://cdn.ipernity.com/200/30/56/50253056.040e95e0.2048.jpg?r2", 2048, 1366), queryResult.getEntity()
            .getThumbnailOriginal());
    }

    @Test
    void describeFail() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("123");

        assertThrows(ConnectorException.class, () -> connector.describe(query).await().indefinitely());
    }
}
