package de.l3s.interweb.connector.openai;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.Choice;
import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.core.chat.Message;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@QuarkusTest
class OpenaiConnectorTest {
    private static final Logger log = Logger.getLogger(OpenaiConnectorTest.class);

    @Inject
    OpenaiConnector connector;

    @Test
    void validate() throws ConnectorException {
        assertTrue(connector.validate());
    }

    @Test
    void completions() throws ConnectorException {
        CompletionsQuery query = new CompletionsQuery();
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?", Message.Role.user);

        CompletionsResults results = connector.completions(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        log.infov("user: {0}", query.getMessages().getLast().getContent());
        for (Choice result : results.getChoices()) {
            log.infov("assistant: {0}", result.getMessage().getContent());
        }
    }
}
