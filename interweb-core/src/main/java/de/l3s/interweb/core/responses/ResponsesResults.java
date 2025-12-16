package de.l3s.interweb.core.responses;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.l3s.interweb.core.chat.Tool;
import de.l3s.interweb.core.chat.ToolChoice;
import de.l3s.interweb.core.chat.Usage;
import de.l3s.interweb.core.chat.UsageCost;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "object", "created_at", "model", "status", "output", "usage", "estimated_cost", "elapsed_time"})
public class ResponsesResults {
    @JsonProperty("background")
    private Boolean background;

    @JsonProperty("created_at")
    private Integer createdAt;

    @JsonProperty("error")
    private ResponseError error;

    @JsonProperty("id")
    private String id;

    @JsonProperty("incomplete_details")
    private ResponseIncompleteDetails incompleteDetails;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    @JsonProperty("max_tool_calls")
    private Integer maxToolCalls;

    @JsonProperty("metadata")
    private Map<String, String> metadata;

    @JsonProperty("model")
    private String model;

    @JsonProperty("object")
    private String object = "response";

    @JsonProperty("output")
    private List<ResponseOutput> output;

    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;

    @JsonProperty("previous_response_id")
    private String previousResponseId;

    @JsonProperty("prompt")
    private ResponsePrompt prompt;

    @JsonProperty("reasoning")
    private ResponseReasoning reasoning;

    @JsonProperty("status")
    private String status;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("text")
    private ResponseText text;

    @JsonProperty("tool_choice")
    private ToolChoice toolChoice;

    @JsonProperty("tools")
    private List<Tool> tools;

    @JsonProperty("top_logprobs")
    private Integer topLogprobs;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("truncation")
    private String truncation;

    @JsonProperty("usage")
    private Usage usage;

    @JsonProperty("user")
    private String user;

    @JsonProperty(value = "estimated_cost")
    private UsageCost cost;

    @JsonProperty("elapsed_time")
    private Long elapsedTime;

    public Boolean getBackground() {
        return background;
    }

    public void setBackground(Boolean background) {
        this.background = background;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public ResponseError getError() {
        return error;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResponseIncompleteDetails getIncompleteDetails() {
        return incompleteDetails;
    }

    public void setIncompleteDetails(ResponseIncompleteDetails incompleteDetails) {
        this.incompleteDetails = incompleteDetails;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(Integer maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public Integer getMaxToolCalls() {
        return maxToolCalls;
    }

    public void setMaxToolCalls(Integer maxToolCalls) {
        this.maxToolCalls = maxToolCalls;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<ResponseOutput> getOutput() {
        return output;
    }

    public void setOutput(List<ResponseOutput> output) {
        this.output = output;
    }

    public Boolean getParallelToolCalls() {
        return parallelToolCalls;
    }

    public void setParallelToolCalls(Boolean parallelToolCalls) {
        this.parallelToolCalls = parallelToolCalls;
    }

    public String getPreviousResponseId() {
        return previousResponseId;
    }

    public void setPreviousResponseId(String previousResponseId) {
        this.previousResponseId = previousResponseId;
    }

    public ResponsePrompt getPrompt() {
        return prompt;
    }

    public void setPrompt(ResponsePrompt prompt) {
        this.prompt = prompt;
    }

    public ResponseReasoning getReasoning() {
        return reasoning;
    }

    public void setReasoning(ResponseReasoning reasoning) {
        this.reasoning = reasoning;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public ResponseText getText() {
        return text;
    }

    public void setText(ResponseText text) {
        this.text = text;
    }

    public ToolChoice getToolChoice() {
        return toolChoice;
    }

    public void setToolChoice(ToolChoice toolChoice) {
        this.toolChoice = toolChoice;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public Integer getTopLogprobs() {
        return topLogprobs;
    }

    public void setTopLogprobs(Integer topLogprobs) {
        this.topLogprobs = topLogprobs;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public String getTruncation() {
        return truncation;
    }

    public void setTruncation(String truncation) {
        this.truncation = truncation;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UsageCost getCost() {
        return cost;
    }

    public void setCost(UsageCost cost) {
        this.cost = cost;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
