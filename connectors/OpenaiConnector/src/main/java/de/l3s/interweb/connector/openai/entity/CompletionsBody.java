package de.l3s.interweb.connector.openai.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.CompletionsQuery;
import de.l3s.interweb.core.chat.ResponseFormat;
import de.l3s.interweb.core.chat.Tool;
import de.l3s.interweb.core.util.Objects;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CompletionsBody {

    private List<OpenaiMessage> messages;

    private Double temperature;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    @JsonProperty("max_completion_tokens")
    private Integer maxCompletionTokens;

    private Integer n;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    private List<Tool> tools;

    @JsonProperty("tool_choice")
    private Object toolChoice;

    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;

    private String[] stop;

    public CompletionsBody(CompletionsQuery query) {
        this.messages = query.getMessages().stream().map(OpenaiMessage::new).toList();
        this.temperature = query.getTemperature();
        this.topP = query.getTopP();
        this.frequencyPenalty = query.getPresencePenalty();
        this.presencePenalty = query.getPresencePenalty();
        this.maxCompletionTokens = Objects.firstNonNull(query.getMaxCompletionTokens(), query.getMaxTokens());
        this.n = query.getN();
        this.responseFormat = query.getResponseFormat();
        this.stop = query.getStop();
        this.tools = query.getTools();
        this.toolChoice = query.getToolChoice();
        this.parallelToolCalls = query.getParallelToolCalls();
    }

    public List<OpenaiMessage> getMessages() {
        return messages;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public Integer getMaxCompletionTokens() {
        return maxCompletionTokens;
    }

    public Integer getN() {
        return n;
    }

    public ResponseFormat getResponseFormat() {
        return responseFormat;
    }

    public String[] getStop() {
        return stop;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public Object getToolChoice() {
        return toolChoice;
    }

    public Boolean getParallelToolCalls() {
        return parallelToolCalls;
    }
}
