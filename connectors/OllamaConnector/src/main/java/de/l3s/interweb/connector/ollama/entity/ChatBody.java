package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import de.l3s.interweb.core.completion.ResponseFormat;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.completion.CompletionQuery;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ChatBody {

    private String model;
    private List<OllamaMessage> messages;
    private String format;
    private OllamaModelOptions options;
    private Boolean stream = false;
    @JsonProperty("keep_alive")
    private String keepAlive;

    public ChatBody(CompletionQuery query) {
        this.model = query.getModel();

        this.messages = query.getMessages().stream()
            .map(OllamaMessage::new)
            .toList();

        this.options = new OllamaModelOptions(query);

        if (query.getResponseFormat() != null && query.getResponseFormat().getType() == ResponseFormat.ResponseType.json_object) {
            this.format = "json";
        }
    }

    public String getModel() {
        return model;
    }

    public List<OllamaMessage> getMessages() {
        return messages;
    }

    public String getFormat() {
        return format;
    }

    public OllamaModelOptions getOptions() {
        return options;
    }

    public Boolean getStream() {
        return stream;
    }

    public String getKeepAlive() {
        return keepAlive;
    }
}
