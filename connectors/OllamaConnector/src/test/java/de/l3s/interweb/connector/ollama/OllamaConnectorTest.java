package de.l3s.interweb.connector.ollama;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.Choice;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;
import de.l3s.interweb.core.completion.Message;


@Disabled
@QuarkusTest
class OllamaConnectorTest {
    private static final Logger log = Logger.getLogger(OllamaConnectorTest.class);

    @Inject
    OllamaConnector connector;

    @Test
    void validate() throws ConnectorException {
        assertTrue(connector.validate());
    }

    @Test
    void complete() throws ConnectorException {
        connector.validate();
        CompletionQuery query = new CompletionQuery();
        query.addMessage("You are Interweb Assistant, a helpful chat bot. Your name is not Claude it is Interweb Assistant.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);
        query.setMaxTokens(100);
        query.setTemperature(20.0);
        query.setTopP(1.0);

        query.setModel("llama3");

        CompletionResults results = connector.complete(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        log.infov("user: {0}", query.getMessages().getLast().getContent());
        for (Choice result : results.getChoices()) {
            log.infov("assistant: {0}", result.getMessage().getContent());
        }
    }
}
