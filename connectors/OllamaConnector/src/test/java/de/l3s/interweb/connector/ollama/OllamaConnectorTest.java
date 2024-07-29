package de.l3s.interweb.connector.ollama;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
    void completions() throws ConnectorException {
        CompletionsQuery query = new CompletionsQuery();
        query.setModel("llama3");
        query.addMessage("You are Interweb Assistant, a helpful chat bot. Your name is not Claude it is Interweb Assistant.", Message.Role.system);
        query.addMessage("What is your name?", Message.Role.user);
        query.setMaxTokens(100);
        query.setTemperature(20.0);
        query.setTopP(1.0);

        long start = System.currentTimeMillis();
        CompletionsResults results = connector.completions(query).await().indefinitely();
        log.infov("duration: {0} ms", System.currentTimeMillis() - start);

        assertEquals(1, results.getChoices().size());
        log.infov("user: {0}", query.getMessages().getLast().getContent());
        for (Choice result : results.getChoices()) {
            log.infov("assistant: {0}", result.getMessage().getContent());
        }
    }

    @Test
    void completionsStream() throws ConnectorException {
        CompletionsQuery query = new CompletionsQuery();
        query.setModel("llama3");
        query.addMessage("You are Interweb Assistant, a helpful chat bot. Your name is not Claude it is Interweb Assistant.", Message.Role.system);
        query.addMessage("What is your name?", Message.Role.user);

        long start = System.currentTimeMillis();
        List<CompletionsResults> list = connector.completionsStream(query).onItem().invoke(() -> {
            log.infov("message after: {0} ms", System.currentTimeMillis() - start);
        }).collect().asList().await().indefinitely();
        log.infov("duration: {0} ms", System.currentTimeMillis() - start);
        assertTrue(list.size() > 10);

        StringBuilder sb = new StringBuilder();
        for (CompletionsResults results : list) {
            for (Choice result : results.getChoices()) {
                sb.append(result.getMessage().getContent());
            }
        }
        log.info(sb.toString());
    }
}
