package de.l3s.interweb.connector.anthropic.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.core.chat.Role;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public final class CompletionBody {

    private List<CompletionMessage> messages;

    private String model;

    private String system;

    private Double temperature;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    public CompletionBody(CompletionsQuery query) {
        this.model = query.getModel();

        this.messages = query.getMessages().stream()
            .filter(m -> m.getRole() != Role.system)
            .map(CompletionMessage::new)
            .toList();
        this.system = query.getMessages().stream()
            .filter(m -> m.getRole() == Role.system)
            .findFirst()
            .map(Message::getContent)
            .orElse(null);

        this.temperature = query.getTemperature();
        this.topP = query.getTopP();
        this.maxTokens = query.getMaxTokens();

        if (this.maxTokens == null) {
            this.maxTokens = 800;
        }
    }

    public String getModel() {
        return model;
    }

    public List<CompletionMessage> getMessages() {
        return messages;
    }

    public String getSystem() {
        return system;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }
}
