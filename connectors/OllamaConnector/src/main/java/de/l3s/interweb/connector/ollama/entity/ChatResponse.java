package de.l3s.interweb.connector.ollama.entity;

import java.time.Instant;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.*;

@RegisterForReflection
public class ChatResponse {

    private String model;
    private Message message;
    @JsonProperty("total_duration")
    private Long totalDuration;
    @JsonProperty("load_duration")
    private Long loadDuration;
    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;
    @JsonProperty("prompt_eval_duration")
    private Long promptEvalDuration;
    @JsonProperty("eval_count")
    private Integer evalCount;
    @JsonProperty("eval_duration")
    private Long evalDuration;
    @JsonProperty("logprobs")
    private List<Logprob> logprobs;
    private Boolean done;
    @JsonProperty("done_reason")
    private String doneReason;
    @JsonProperty("created_at")
    private String createdAt;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(Long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public Integer getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(Integer promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public Long getPromptEvalDuration() {
        return promptEvalDuration;
    }

    public void setPromptEvalDuration(Long promptEvalDuration) {
        this.promptEvalDuration = promptEvalDuration;
    }

    public Integer getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(Integer evalCount) {
        this.evalCount = evalCount;
    }

    public Long getEvalDuration() {
        return evalDuration;
    }

    public void setEvalDuration(Long evalDuration) {
        this.evalDuration = evalDuration;
    }

    public List<Logprob> getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(List<Logprob> logprobs) {
        this.logprobs = logprobs;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getDoneReason() {
        return doneReason;
    }

    public void setDoneReason(String doneReason) {
        this.doneReason = doneReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public CompletionsResults toCompletionResults() {
        CompletionsResults results = new CompletionsResults();
        results.setModel(model);

        if (promptEvalCount != null && evalCount != null) {
            Usage usage = new Usage(promptEvalCount, evalCount);
            results.setUsage(usage);
        }

        if (message != null) {
            Choice choice = new Choice(0, doneReason, message.toMessage());
            if (this.getLogprobs() != null) {
                Logprobs logprobs = new Logprobs();
                logprobs.setContent(this.getLogprobs());
                choice.setLogprobs(logprobs);
            }
            results.setChoices(List.of(choice));
        }

        if (totalDuration != null || loadDuration != null || promptEvalDuration != null || evalDuration != null) {
            Duration duration = new Duration();
            duration.setTotal(totalDuration);
            duration.setLoad(loadDuration);
            duration.setPromptEvaluation(promptEvalDuration);
            duration.setCompletionGeneration(evalDuration);
            results.setDuration(duration);
        }

        if (createdAt != null) {
            results.setCreated(Instant.parse(createdAt));
        }

        return results;
    }
}
