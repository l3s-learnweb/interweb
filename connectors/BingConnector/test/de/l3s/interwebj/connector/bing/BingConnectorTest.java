package de.l3s.interwebj.connector.bing;

import de.l3s.bingService.models.*;
import de.l3s.bingService.models.query.BingQuery;
import de.l3s.bingService.services.BingApiService;
import de.l3s.interwebj.core.AuthCredentials;
import de.l3s.interwebj.core.InterWebException;
import de.l3s.interwebj.core.core.AbstractServiceConnector;
import de.l3s.interwebj.core.query.Query;
import de.l3s.interwebj.core.query.QueryFactory;
import de.l3s.interwebj.core.query.QueryResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BingConnectorTest {
    private final static String TEST_KEY = "***REMOVED***";
    private final static String TEST_SECRET = "";

    private static AbstractServiceConnector connector;

    @BeforeAll
    public static void initialize() {
        AuthCredentials consumerAuthCredentials = new AuthCredentials(TEST_KEY, TEST_SECRET);
        connector = new BingConnector(consumerAuthCredentials);
    }

    @Test
    void get() throws InterWebException {
        QueryFactory queryFactory = new QueryFactory();
        Query query = queryFactory.createQuery("hello world");
        query.addContentType(Query.CT_TEXT);
        query.addContentType(Query.CT_IMAGE);
        query.setResultCount(5);
        //		query.addParam("date_from", "2009-01-01 00:00:00");
        //		query.addParam("date_till", "2009-06-01 00:00:00");
        query.setSortOrder(Query.SortOrder.RELEVANCE);

        QueryResult queryResult = connector.get(query, null);

        assertEquals(20, queryResult.getResultItems().size());
        assertTrue(queryResult.getTotalResultCount() > 100);
    }

    // @Test
    void testService() throws InterWebException, IOException {
        BingQuery query = new BingQuery();
        query.setQuery("clinton");
        query.setMarket("en-US");
        query.setCount(60);
        query.setOffset(0);

        //195f994bbef145c587ae13df58b33c9a api 5
        BingResponse response = new BingApiService(TEST_KEY).getResponseFromBingApi(query);

        WebPagesMainHolder pages = response.getWebPages();
        System.out.println("web pages: " + pages.getTotalEstimatedMatches());

        int counter = 1;
        for(WebPage page : pages.getValue())
        {
            System.out.println(page);
            System.out.println(counter++);
        }

        System.out.println("-----------");
        ImageHolder images = response.getImages();

        if(images != null)
        {
            for(Image image : images.getValue())
            {
                System.out.println(image.getContentSize());
                System.out.println(image.getMedia());
            }
        }

        System.out.println(response.getJsonContent());
    }
}