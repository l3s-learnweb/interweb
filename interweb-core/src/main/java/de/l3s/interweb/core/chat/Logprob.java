package de.l3s.interweb.core.chat;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class Logprob {
    /**
     * The token.
     */
    private String token;
    /**
     * The log probability of this token, if it is within the top 20 most likely tokens.
     * Otherwise, the value -9999.0 is used to signify that the token is very unlikely.
     */
    private double logprob;
    /**
     * A list of integers representing the UTF-8 bytes representation of the token.
     * Useful in instances where characters are represented by multiple tokens and their byte
     * representations must be combined to generate the correct text representation.
     * Can be null if there is no bytes representation for the token.
     */
    private List<Integer> bytes;
    /**
     * List of the most likely tokens and their log probability, at this token position.
     * In rare cases, there may be fewer than the number of requested top_logprobs returned.
     */
    @JsonProperty("top_logprobs")
    private List<Logprob> topLogprobs;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getLogprob() {
        return logprob;
    }

    public void setLogprob(double logprob) {
        this.logprob = logprob;
    }

    public List<Integer> getBytes() {
        return bytes;
    }

    public void setBytes(List<Integer> bytes) {
        this.bytes = bytes;
    }

    public List<Logprob> getTopLogprobs() {
        return topLogprobs;
    }

    public void setTopLogprobs(List<Logprob> topLogprobs) {
        this.topLogprobs = topLogprobs;
    }
}
