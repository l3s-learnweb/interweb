package de.l3s.interweb.core.responses;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.Logprob;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOutputMessageContent {
    private String type;

    @JsonProperty("annotations")
    private List<ResponseAnnotation> annotations;

    @JsonProperty("logprobs")
    private List<Logprob> logprobs;

    @JsonProperty("text")
    private String text;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ResponseAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<ResponseAnnotation> annotations) {
        this.annotations = annotations;
    }

    public List<Logprob> getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(List<Logprob> logprobs) {
        this.logprobs = logprobs;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
