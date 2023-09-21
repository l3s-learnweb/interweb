package de.l3s.interweb.connector.flickr;

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
class FlickrConnectorTest {
    private static final Logger log = Logger.getLogger(FlickrConnectorTest.class);

    @Inject
    FlickrConnector connector;

    @Test
    void search() throws ConnectorException {
        SearchQuery query = new SearchQuery();
        query.setQuery("hello world");
        query.addContentType(ContentType.image);
        // query.addContentType(ContentType.video);
        query.setPerPage(50);
        // query.setDateFrom("2009-01-01 00:00:00");
        // query.setDateTo("2009-06-01 00:00:00");
        query.setSort(SearchSort.relevance);

        SearchConnectorResults queryResult = connector.search(query).await().indefinitely();

        for (SearchItem res : queryResult.getItems()) {
            log.info(res);
        }

        assertEquals(49, queryResult.getItems().size());
        assertTrue(queryResult.getTotalResults() > 100);
    }

    @Test
    void findIds() {
        assertEquals("kbVEPF", connector.findId("https://flic.kr/p/kbVEPF"));
        assertEquals("26315753363", connector.findId("https://www.flickr.com/photos/marcelasl/26315753363/"));
        assertEquals("12594415085", connector.findId("https://www.flickr.com/photos/49729828@N08/12594415085/in/gallery-flickr-72157722053971868/"));
        assertEquals("53138136821", connector.findId("https://www.flickr.com/photos/183763650@N04/53138136821/in/photolist-2oXXMWb-2oXSMvs-2p2uoLd-2oZzrvR-2p3Zg5N-2oYsg4f-2oZhZWV-2p2SfwJ-2oZJYPy-2oXCQLM-2oXpkTy-2oYHXkT-2oXBua1-2p1wFtM-2p3Xu7j-2oYBWBn-2oYvfjG-2p3nPXL-2p3w9oj-2oYd9bn-2oZfF2b-2p3yHVN-2p24yX6-2p2WWj4-2p24j57-2oZwuXF-2oZoszW-2p2TuZb-2p1UzEp-2p3recF-2oYjs3c-2p3xipD-2oZAU5x-2oZVwcK-2oZJ8W9-2p3moMF-2p1jMae-2oXoyor-2p1CNXL-2p2QMVV-2p3c37o-2p2eUnW-2oZrr89-2p4aqS6-2oXRkRB-2p3qGrU-2p3vmUG-2p29V2k-2p33Yyr-2oX5ZwK"));
    }

    @Test
    void describe() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("53173916413");

        DescribeResults queryResult = connector.describe(query).await().indefinitely();

        assertEquals("53173916413", queryResult.getEntity().getId());
        assertEquals("https://flickr.com/photos/189420050@N03/53173916413", queryResult.getEntity().getUrl());
        assertEquals(ContentType.image, queryResult.getEntity().getType());
        assertEquals("Swallows in the Spring Orchard", queryResult.getEntity().getTitle());
        assertNotNull(queryResult.getEntity().getDescription());
        assertEquals(Instant.ofEpochSecond(1694201930L), queryResult.getEntity().getDate());
        assertEquals("Irene Steeves", queryResult.getEntity().getAuthor());
        assertEquals("https://www.flickr.com/photos/189420050@N03", queryResult.getEntity().getAuthorUrl());
        assertTrue(queryResult.getEntity().getCommentsCount() > 125);
        assertTrue(queryResult.getEntity().getViewsCount() > 1390);
        assertEquals(new Thumbnail("https://live.staticflickr.com/65535/53173916413_a31beacb93_m.jpg", 240, null), queryResult.getEntity().getThumbnailSmall());
        assertEquals(new Thumbnail("https://live.staticflickr.com/65535/53173916413_a31beacb93.jpg", 500, null), queryResult.getEntity().getThumbnailMedium());
        assertEquals(new Thumbnail("https://live.staticflickr.com/65535/53173916413_a31beacb93_b.jpg", 1024, null), queryResult.getEntity().getThumbnailLarge());
        // assertEquals(new Thumbnail("https://live.staticflickr.com/65535/53173916413_a31beacb93_o.jpg"), queryResult.getEntity().getThumbnailOriginal());
    }

    @Test
    void describeFail() throws ConnectorException {
        DescribeQuery query = new DescribeQuery();
        query.setId("dsadasdasda");

        assertThrows(ConnectorException.class, () -> connector.describe(query).await().indefinitely());
    }
}
