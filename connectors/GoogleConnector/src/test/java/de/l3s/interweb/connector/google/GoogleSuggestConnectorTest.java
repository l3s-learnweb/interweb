package de.l3s.interweb.connector.google;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jboss.logging.Logger;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.suggest.SuggestConnector;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Disabled
class GoogleSuggestConnectorTest {
    private static final Logger log = Logger.getLogger(GoogleSuggestConnectorTest.class);
    private static final SuggestConnector connector = new GoogleSuggestConnector();

    @Test
    void query() throws ConnectorException {
        SuggestQuery query = new SuggestQuery();
        query.setQuery("nikola tesla");
        query.setLanguage("en");

        SuggestConnectorResults results = connector.suggest(query);

        assertEquals(10, results.size());
        System.out.println("Results for '" + query.getQuery() + "':");
        for (String result : results.getItems()) {
            System.out.println(result);
        }
    }
}
