package de.l3s.interweb.core.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class CompletionQuery {

    /**
     * ID of the model to use.
     * Defaults to "gpt-35-turbo".
     */
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String model = "gpt-35-turbo";

    /**
     * ID of the chat to continue.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID chatId;

    /**
     * A list of messages comprising the conversation so far.
     */
    @NotEmpty
    private List<Message> messages = new ArrayList<>();

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     * <br/>
     * We generally recommend altering this or `top_p` but not both.
     */
    @Min(0)
    @Max(2)
    private Double temperature = 1.0;

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results
     * of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     * <br/>
     * We generally recommend altering this or `temperature` but not both.
     */
    @Min(0)
    @Max(1)
    @JsonProperty("top_p")
    private Double topP = 1.0;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far,
     * decreasing the model's likelihood to repeat the same line verbatim.
     */
    @Min(-2)
    @Max(2)
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty = 0.0;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far,
     * increasing the model's likelihood to talk about new topics.
     */
    @Min(-2)
    @Max(2)
    @JsonProperty("presence_penalty")
    private Double presencePenalty = 0.0;

    /**
     * The maximum number of tokens to generate in the chat completion. Defaults to 800.
     * <br/>
     * The total length of input tokens and generated tokens is limited by the model's context length.
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens = 800;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(final List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(final String message, final Message.Role role) {
        this.messages.add(new Message(role, message));
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
}
