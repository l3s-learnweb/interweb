package de.l3s.interweb.connector.ollama;

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
        query.setModel("llama3.1:8b");
        query.addMessage("You are Interweb Assistant, a helpful chat bot. Your name is not Claude it is Interweb Assistant.", Role.system);
        query.addMessage("What is your name?", Role.user);
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
        query.setModel("llama3.1:8b");
        query.addMessage("You are Interweb Assistant, a helpful chat bot. Your name is not Claude it is Interweb Assistant.", Role.system);
        query.addMessage("What is your name?", Role.user);

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

    @Test
    void completionsWithTool() throws ConnectorException {
        Tool weatherTool = Tool.functionBuilder()
            .name("get_weather")
            .description("Return the weather in a city.")
            .properties(
                city -> city.name("city").type("string").description("The city name.").required(),
                duration -> duration.name("duration").type("integer").description("Number of days to return.").required()
            )
            .build();

        CompletionsQuery query = new CompletionsQuery();
        query.setModel("llama3.1:8b");
        query.setTools(List.of(weatherTool));
        query.setToolChoice(ToolChoice.required);
        query.addMessage("You are Interweb Assistant, a helpful chat bot.", Role.system);
        query.addMessage("What is the weather in Hannover?", Role.user);

        CompletionsResults results = connector.completions(query).await().indefinitely();

        assertEquals(1, results.getChoices().size());
        for (Choice result : results.getChoices()) {
            assertNotNull(result.getMessage().getToolCalls());
            CallFunction fn = result.getMessage().getToolCalls().getFirst().getFunction();
            assertNotNull(fn);
            assertEquals("get_weather", fn.getName());
            assertEquals("Hannover", fn.getArguments().get("city"));
            assertNotNull(fn.getArguments().get("duration"));
        }
    }
}
