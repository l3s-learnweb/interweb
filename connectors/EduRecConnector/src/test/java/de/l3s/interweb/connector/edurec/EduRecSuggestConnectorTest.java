package de.l3s.interweb.connector.edurec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.suggest.SuggestConnectorResults;
import de.l3s.interweb.core.suggest.SuggestQuery;

@Disabled
class EduRecSuggestConnectorTest {

    private static final EduRecSuggestConnector provider = new EduRecSuggestConnector();

    @Test
    void query() throws ConnectorException {
        SuggestQuery query = new SuggestQuery();
        query.setQuery("nikola tesla");

        SuggestConnectorResults results = provider.suggest(query);

        assertEquals(10, results.size());
        for (String result : results.getItems()) {
            System.out.println(result);
        }
    }
}
