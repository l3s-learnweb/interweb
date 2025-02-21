package de.l3s.interweb.core.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class CompletionsQuery {

    /**
     * ID of the model to use. Use `GET /models` to retrieve available models.
     */
    @NotEmpty
    @JsonProperty("model")
    private String model;

    /**
     * ID of the chat to continue.
     */
    @JsonProperty("id")
    private UUID id;

    /**
     * Participant involved in the conversation can be denoted by its ID.
     * When provided, it can be used to filter the conversations.
     * Applied automatically when using chatId.
     */
    @JsonProperty(value = "user")
    private String user;

    /**
     * A list of messages comprising the conversation so far.
     */
    @NotEmpty
    @JsonProperty("messages")
    private List<Message> messages = new ArrayList<>();

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     * <br/>
     * We generally recommend altering this or `top_p` but not both.
     */
    @Min(0)
    @Max(2)
    @JsonProperty("temperature")
    private Double temperature;

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results
     * of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     * <br/>
     * We generally recommend altering this or `temperature` but not both.
     */
    @Min(0)
    @Max(1)
    @JsonProperty("top_p")
    private Double topP;

    /**
     * Alternative to the top_p, and aims to ensure a balance of quality and variety.
     * The parameter p represents the minimum probability for a token to be considered, relative to the probability of the most likely token.
     * For example, with p=0.05 and the most likely token having a probability of 0.9, logits with a value less than 0.045 are filtered out. (Default: 0.0)
     * Available via Ollama on certain models.
     */
    @Min(0)
    @Max(1)
    @JsonProperty("min_p")
    private Double minP;

    /**
     * Reduces the probability of generating nonsense. A higher value (e.g. 100) will give more diverse answers,
     * while a lower value (e.g. 10) will be more conservative. (Default: 40)
     * Available via Ollama on certain models.
     */
    @Min(0)
    @Max(100)
    @JsonProperty("top_k")
    private Integer topK;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far,
     * decreasing the model's likelihood to repeat the same line verbatim.
     */
    @Min(-2)
    @Max(2)
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far,
     * increasing the model's likelihood to talk about new topics.
     */
    @Min(-2)
    @Max(2)
    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    /**
     * The maximum number of tokens to generate in the chat completion. Defaults to 800.
     * <br/>
     * The total length of input tokens and generated tokens is limited by the model's context length.
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * Sets the size of the context window used to generate the next token. Defaults to 2048.
     */
    @JsonProperty("num_ctx")
    private Integer numCtx;

    /**
     * A list of tools the model may call. Currently, only functions are supported as a tool.
     * Use this to provide a list of functions the model may generate JSON inputs for.
     * <br/>
     * A max of 128 functions are supported.
     */
    @Size(max = 128)
    @JsonProperty("tools")
    private List<Tool> tools;

    /**
     * Controls which (if any) tool is called by the model.
     *  - none means the model will not call any tool and instead generates a message.
     *  - auto means the model can pick between generating a message or calling one or more tools.
     *  - required means the model must call one or more tools.
     * Specifying a particular tool via {"type": "function", "function": {"name": "my_function"}} forces the model to call that tool.
     * <br/>
     * none is the default when no tools are present. auto is the default if tools are present.
     */
    @JsonProperty("tool_choice")
    private Object toolChoice;

    /**
     * Whether to enable parallel function calling during tool use.
     * https://platform.openai.com/docs/guides/function-calling/parallel-function-calling
     */
    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;

    /**
     * Whether to incrementally stream the response using server-sent events. Defaults to false.
     */
    private boolean stream = false;

    /**
     * Whether the conversation should be saved on Interweb. Defaults to false.
     */
    private boolean save = false;

    /**
     * How many completions to generate for each prompt. Minimum of 1 (default) and maximum of 128 allowed.
     * Note: Because this parameter generates many completions, it can quickly consume your token quota.
     */
    private Integer n;

    /**
     * If specified, our system will make the best effort to sample deterministically,
     * such that repeated requests with the same seed and parameters should return the same result.
     * Determinism isn't guaranteed, and you should refer to the system_fingerprint response parameter to monitor changes in the backend.
     */
    private Integer seed;

    /**
     * An object specifying the format that the model must output. Used to enable JSON mode.
     */
    private ResponseFormat responseFormat;

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     */
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private String[] stop;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(final List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(final Message message) {
        this.messages.add(message);
    }

    public void addMessage(final String message, final Role role) {
        addMessage(new Message(role, message));
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(final Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(final Double topP) {
        this.topP = topP;
    }

    public Double getMinP() {
        return minP;
    }

    public void setMinP(Double minP) {
        this.minP = minP;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(final Integer topK) {
        this.topK = topK;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(final Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(final Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(final Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getNumCtx() {
        return numCtx;
    }

    public void setNumCtx(Integer numCtx) {
        this.numCtx = numCtx;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getN() {
        return n;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public void setToolChoice(Object toolChoice) {
        this.toolChoice = toolChoice;
    }

    public Object getToolChoice() {
        return toolChoice;
    }

    public void setParallelToolCalls(Boolean parallelToolCalls) {
        this.parallelToolCalls = parallelToolCalls;
    }

    public Boolean getParallelToolCalls() {
        return parallelToolCalls;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setResponseFormat(ResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }

    public ResponseFormat getResponseFormat() {
        return responseFormat;
    }

    public void setStop(String[] stop) {
        this.stop = stop;
    }

    public String[] getStop() {
        return stop;
    }
}
