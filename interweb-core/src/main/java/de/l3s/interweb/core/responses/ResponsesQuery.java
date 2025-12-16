package de.l3s.interweb.core.responses;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.l3s.interweb.core.chat.Tool;
import de.l3s.interweb.core.chat.ToolChoice;
import de.l3s.interweb.core.util.StringOrArrayDeserializer;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsesQuery {

    /**
     * Whether to run the model response in the background.
     */
    @JsonProperty("background")
    private Boolean background = false;

    /**
     * Specify additional output data to include in the model response.
     *
     * Currently supported values are:
     * - code_interpreter_call.outputs: Includes the outputs of python code execution in code interpreter tool call items.
     * - computer_call_output.output.image_url: Include image urls from the computer call output.
     * - file_search_call.results: Include the search results of the file search tool call.
     * - message.input_image.image_url: Include image urls from the input message.
     * - message.output_text.logprobs: Include logprobs with assistant messages.
     * - reasoning.encrypted_content: Includes an encrypted version of reasoning tokens in reasoning item outputs.
     *      This enables reasoning items to be used in multi-turn conversations when using the Responses API statelessly
     *      (like when the store parameter is set to false, or when an organization is enrolled in the zero data retention program).
     */
    @JsonProperty("include")
    private List<String> include;

    /**
     * Input text to embed, encoded as a string or array of tokens.
     * To embed multiple inputs in a single request, pass an array of strings or array of token arrays.
     */
    @JsonProperty("input")
    @JsonDeserialize(using = StringOrArrayDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<String> input;

    /**
     * A system (or developer) message inserted into the model's context.
     *
     * When using along with previous_response_id, the instructions from a previous
     * response will not be carried over to the next response. This makes it simple
     * to swap out system (or developer) messages in new responses.
     */
    @JsonProperty("instructions")
    private String instructions;

    /**
     * An upper bound for the number of tokens that can be generated for a response, including visible output tokens and reasoning tokens.
     */
    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    /**
     * The maximum number of total calls to built-in tools that can be processed in a response.
     */
    @JsonProperty("max_tool_calls")
    private Integer maxToolCalls;

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     */
    @JsonProperty("metadata")
    private Map<String, String> metadata;

    /**
     * The model deployment to use for the creation of this response.
     */
    @NotEmpty
    @JsonProperty("model")
    private String model;

    /**
     * Whether to allow the model to run tool calls in parallel.
     */
    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;

    /**
     * The unique ID of the previous response to the model. Use this to create multi-turn conversations.
     */
    @JsonProperty("previous_response_id")
    private String previousResponseId;

    /**
     * Reference to a prompt template and its variables.
     */
    @JsonProperty("prompt")
    private ResponsePrompt prompt;

    /**
     * Configuration options for reasoning models.
     */
    @JsonProperty("reasoning")
    private ResponseReasoning reasoning;

    /**
     * Whether to store the generated model response for later retrieval via API.
     */
    @JsonProperty("store")
    private Boolean store;

    /**
     * If set to true, the model response data will be streamed to the client as it is generated using server-sent events.
     */
    @JsonProperty("stream")
    private Boolean stream;

    /**
     * What sampling temperature to use, between 0 and 2.
     */
    @Min(0)
    @Max(2)
    @JsonProperty("temperature")
    private Double temperature;

    /**
     * Configuration options for a text response from the model.
     */
    @JsonProperty("text")
    private ResponseText text;

    /**
     * Controls which (if any) tool is called by the model.
     */
    @JsonProperty("tool_choice")
    private ToolChoice toolChoice;

    /**
     * An array of tools the model may call while generating a response.
     */
    @JsonProperty("tools")
    private List<Tool> tools;

    /**
     * An integer between 0 and 20 specifying the number of most likely tokens to return at each token position.
     */
    @Min(0)
    @Max(20)
    @JsonProperty("top_logprobs")
    private Integer topLogprobs;

    /**
     * An alternative to sampling with temperature, called nucleus sampling.
     */
    @Min(0)
    @Max(1)
    @JsonProperty("top_p")
    private Double topP;

    /**
     * The truncation strategy to use for the model response.
     */
    @JsonProperty("truncation")
    private String truncation;

    /**
     * A unique identifier representing your end-user.
     */
    @JsonProperty("user")
    private String user;

    public Boolean getBackground() {
        return background;
    }

    public void setBackground(Boolean background) {
        this.background = background;
    }

    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
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

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
