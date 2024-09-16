package de.l3s.interweb.connector.openai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.chat.*;

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
        query.setModel("gpt-35-turbo");
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Role.system);
        query.addMessage("What is your name?", Role.user);

        CompletionsResults results = connector.completions(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        log.infov("user: {0}", query.getMessages().getLast().getContent());
        for (Choice result : results.getChoices()) {
            log.infov("assistant: {0}", result.getMessage().getContent());
        }
    }

    @Test
    void completionsWithTool() throws ConnectorException {
        Tool weatherTool = Tool.functionBuilder()
            .name("get_weather")
            .description("Return the weather in a city.")
            .parameters(city -> city.name("city").type("string").description("The city name.").required())
            .build();

        CompletionsQuery query = new CompletionsQuery();
        query.setModel("gpt-4");
        query.setTools(List.of(weatherTool));
        query.setToolChoice(ToolChoice.required);
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Role.system);
        query.addMessage("What is the weather in Hannover?", Role.user);

        CompletionsResults results = connector.completions(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        for (Choice result : results.getChoices()) {
            assertEquals("get_weather", result.getMessage().getToolCalls().getFirst().getFunction().getName());
        }
    }
}
