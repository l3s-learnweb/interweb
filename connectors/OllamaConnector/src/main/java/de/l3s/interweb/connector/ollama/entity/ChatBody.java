package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.ResponseFormat;
import de.l3s.interweb.core.chat.Tool;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatBody {

    private String model;
    private List<Message> messages;
    private String format;
    private ModelOptions options;
    private Boolean stream = false;
    @JsonProperty("keep_alive")
    private String keepAlive;
    protected List<Tool> tools;

    private Object think;
    private Boolean logprobs;
    @JsonProperty("top_logprobs")
    private Integer topLogprobs;

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getFormat() {
        return format;
    }

    public ModelOptions getOptions() {
        return options;
    }

    public Boolean getStream() {
        return stream;
    }

    public String getKeepAlive() {
        return keepAlive;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public Object getThink() {
        return think;
    }

    public void setThink(Object think) {
        this.think = think;
    }

    public Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public Integer getTopLogprobs() {
        return topLogprobs;
    }

    public void setTopLogprobs(Integer topLogprobs) {
        this.topLogprobs = topLogprobs;
    }

    public static ChatBody of(CompletionsQuery query) {
        ChatBody body = new ChatBody();
        body.stream = query.isStream() != null && query.isStream();
        body.model = query.getModel();
        body.logprobs = query.getLogprobs();
        body.topLogprobs = query.getTopLogprobs();

        body.messages = query.getMessages().stream()
            .map(Message::of)
            .toList();

        body.options = ModelOptions.of(query);
        body.tools = query.getTools();

        if (query.getResponseFormat() != null && query.getResponseFormat().getType() == ResponseFormat.ResponseType.json_object) {
            body.format = "json";
        }
        return body;
    }
}
