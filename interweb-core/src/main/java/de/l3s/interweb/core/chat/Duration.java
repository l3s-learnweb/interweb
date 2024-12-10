package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class Duration {
    /**
     * Total processing time to generate the response (in nanoseconds)
     */
    private Long total;

    /**
     * Time spent loading the model (in nanoseconds)
     */
    private Long load;

    /**
     * Time spent evaluating the prompt (in nanoseconds)
     */
    @JsonProperty("prompt_eval")
    private Long promptEvaluation;

    /**
     * Time spent generating the response (in nanoseconds)
     */
    @JsonProperty("completion_gen")
    private Long completionGeneration;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getLoad() {
        return load;
    }

    public void setLoad(Long load) {
        this.load = load;
    }

    public Long getPromptEvaluation() {
        return promptEvaluation;
    }

    public void setPromptEvaluation(Long promptEvaluation) {
        this.promptEvaluation = promptEvaluation;
    }

    public Long getCompletionGeneration() {
        return completionGeneration;
    }

    public void setCompletionGeneration(Long completionGeneration) {
        this.completionGeneration = completionGeneration;
    }

    public void add(Duration other) {
        if (total != null && other.total != null) {
            total += other.total;
        }
        if (load != null && other.load != null) {
            load += other.load;
        }
        if (promptEvaluation != null && other.promptEvaluation != null) {
            promptEvaluation += other.promptEvaluation;
        }
        if (completionGeneration != null && other.completionGeneration != null) {
            completionGeneration += other.completionGeneration;
        }
    }

    public static Duration of(long ns) {
        Duration duration = new Duration();
        duration.setTotal(ns);
        return duration;
    }
}
