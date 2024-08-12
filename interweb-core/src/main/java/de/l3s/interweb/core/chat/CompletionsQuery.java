package de.l3s.interweb.core.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class CompletionsQuery {

    /**
     * ID of the model to use.
     * Defaults to "gemma2:9b".
     */
    @NotEmpty
    @JsonProperty("model")
    private String model = "gemma2:9b";

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
