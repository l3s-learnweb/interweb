package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.completion.CompletionQuery;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public final class CompletionBody {

    private String model;

    private List<CompletionMessage> messages;

    private CompletionOptions options;

    private final Boolean stream = false;

    public CompletionBody(CompletionQuery query) {
        this.model = query.getModel();

        this.messages = query.getMessages().stream()
                .map(CompletionMessage::new)
                .toList();
        
        this.options = new CompletionOptions(query);
    }

    public String getModel() {
        return model;
    }

    public List<CompletionMessage> getMessages() {
        return messages;
    }

    public CompletionOptions getOptions() {
        return options;
    }

    public Boolean getStream() {
        return stream;
    }
}
