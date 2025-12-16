package de.l3s.interweb.core.chat;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Logprobs {
    /**
     * A list of message content tokens with log probability information.
     */
    private List<Logprob> content;
    /**
     * A list of message refusal tokens with log probability information.
     */
    private List<Logprob> refusal;

    public List<Logprob> getContent() {
        return content;
    }

    public void setContent(List<Logprob> content) {
        this.content = content;
    }

    public List<Logprob> getRefusal() {
        return refusal;
    }

    public void setRefusal(List<Logprob> refusal) {
        this.refusal = refusal;
    }
}
