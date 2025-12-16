package de.l3s.interweb.connector.ollama.entity;

import java.util.List;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TokenLogprob {
    private String token;
    private Double logprob;
    private List<Integer> bytes;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getLogprob() {
        return logprob;
    }

    public void setLogprob(Double logprob) {
        this.logprob = logprob;
    }

    public List<Integer> getBytes() {
        return bytes;
    }

    public void setBytes(List<Integer> bytes) {
        this.bytes = bytes;
    }
}
