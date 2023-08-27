package de.l3s.interweb.connector.google;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Disabled
@QuarkusTest
class GoogleConnectorTest {
    private static final Logger log = Logger.getLogger(GoogleConnectorTest.class);

    @Inject
    GoogleConnector connector;

    @Test
    void suggest() throws ConnectorException {
        SuggestQuery query = new SuggestQuery();
        query.setQuery("nikola tesla");
        query.setLanguage("en");

        SuggestConnectorResults results = connector.suggest(query).await().indefinitely();

        assertEquals(10, results.size());
        log.infov("Results for '{0}':", query.getQuery());
        for (String result : results.getItems()) {
            log.infov("  {0}", result);
        }
    }
}
