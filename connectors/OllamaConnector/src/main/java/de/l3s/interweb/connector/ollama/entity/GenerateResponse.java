package de.l3s.interweb.connector.ollama.entity;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.Logprob;
import de.l3s.interweb.core.responses.ResponseOutput;
import de.l3s.interweb.core.responses.ResponseOutputMessageContent;
import de.l3s.interweb.core.responses.ResponsesResults;

@RegisterForReflection
public class GenerateResponse {

    private String model;
    @JsonProperty("created_at")
    private String createdAt;
    private String response;
    private String thinking;
    private Boolean done;
    @JsonProperty("done_reason")
    private String doneReason;

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
    private List<Logprob> logprobs;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getThinking() {
        return thinking;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
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

    public ResponsesResults toResponsesResults() {
        ResponsesResults results = new ResponsesResults();
        results.setModel(this.model);
        // results.setCreatedAt(this.createdAt); // Type mismatch String vs Integer
        results.setElapsedTime(this.totalDuration);

        ResponseOutput message = new ResponseOutput();
        message.setRole("assistant");
        message.setStatus(this.done ? "completed" : "in_progress");
        message.setType("message");

        ResponseOutputMessageContent content = new ResponseOutputMessageContent();
        content.setType("output_text");
        content.setText(this.response);
        if (this.logprobs != null) {
            content.setLogprobs(this.logprobs);
        }

        List<ResponseOutputMessageContent> contents = new ArrayList<>();
        contents.add(content);
        message.setContent(contents);

        List<ResponseOutput> outputs = new ArrayList<>();
        outputs.add(message);
        results.setOutput(outputs);

        return results;
    }
}
