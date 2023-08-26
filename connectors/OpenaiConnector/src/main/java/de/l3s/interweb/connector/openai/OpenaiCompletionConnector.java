package de.l3s.interweb.connector.openai;

import jakarta.enterprise.context.Dependent;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.l3s.interweb.connector.openai.client.OpenaiClient;
import de.l3s.interweb.core.AuthCredentials;
import de.l3s.interweb.core.ConnectorException;
import de.l3s.interweb.core.completion.CompletionConnector;
import de.l3s.interweb.core.completion.CompletionQuery;
import de.l3s.interweb.core.completion.CompletionResults;

@Dependent
public class OpenaiCompletionConnector implements CompletionConnector {

    private static final String version = "2023-05-15";

    @Override
    public String getName() {
        return "OpenAI";
    }

    @Override
    public String getBaseUrl() {
        return "https://openai.com/";
    }

    @Override
    public String[] getModels() {
        return new String[]{"gpt-35-turbo", "gpt-35-turbo-16k", "gpt-4"};
    }

    @RestClient
    OpenaiClient openai;

    @Override
    public Uni<CompletionResults> complete(CompletionQuery query, AuthCredentials credentials) throws ConnectorException {
        return openai.chatCompletions(query.getModel(), version, query);
    }
}
