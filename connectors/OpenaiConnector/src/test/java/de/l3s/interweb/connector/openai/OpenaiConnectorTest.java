package de.l3s.interweb.connector.openai;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
class OpenaiConnectorTest {
    private static final Logger log = Logger.getLogger(OpenaiConnectorTest.class);

    @Inject
    OpenaiConnector connector;

    @Test
    void complete() throws ConnectorException {
        CompletionQuery query = new CompletionQuery();
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Message.Role.system);
        query.addMessage("What is your name?.", Message.Role.user);

        CompletionResults results = connector.complete(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        System.out.println("Results for '" + query.getMessages().get(query.getMessages().size() - 1).getContent() + "':");
        for (Choice result : results.getChoices()) {
            System.out.println(result.getMessage().getContent());
        }
    }
}
