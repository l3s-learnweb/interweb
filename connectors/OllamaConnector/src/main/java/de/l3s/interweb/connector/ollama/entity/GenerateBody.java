package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.ResponseFormat;
import de.l3s.interweb.core.responses.ResponsesQuery;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateBody {

    private String model;
    private String prompt;
    private String suffix;
    private List<String> images;
    private String format;
    private String system;
    private Boolean stream = false;
    private Boolean raw;
    @JsonProperty("keep_alive")
    private String keepAlive;
    private ModelOptions options;
    private Object think;
    private Boolean logprobs;
    @JsonProperty("top_logprobs")
    private Integer topLogprobs;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public Boolean getRaw() {
        return raw;
    }

    public void setRaw(Boolean raw) {
        this.raw = raw;
    }

    public String getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(String keepAlive) {
        this.keepAlive = keepAlive;
    }

    public ModelOptions getOptions() {
        return options;
    }

    public void setOptions(ModelOptions options) {
        this.options = options;
    }

    public Object getThink() {
        return think;
    }

    public void setThink(Object think) {
        this.think = think;
    }

    public Boolean getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Boolean logprobs) {
        this.logprobs = logprobs;
    }

    public Integer getTopLogprobs() {
        return topLogprobs;
    }

    public void setTopLogprobs(Integer topLogprobs) {
        this.topLogprobs = topLogprobs;
    }

    public static GenerateBody of(ResponsesQuery query) {
        GenerateBody body = new GenerateBody();
        body.setModel(query.getModel());
        if (query.getInput() != null && !query.getInput().isEmpty()) {
            body.setPrompt(query.getInput().get(0));
        }
        body.setSystem(query.getInstructions());
        body.setStream(query.getStream());
        body.setOptions(ModelOptions.of(query));
        body.setTopLogprobs(query.getTopLogprobs());
        if (query.getText() != null && query.getText().getFormat() != null) {
            body.setFormat(query.getText().getFormat().getType() == ResponseFormat.ResponseType.json_object ? "json" : null);
        }
        return body;
    }
}
