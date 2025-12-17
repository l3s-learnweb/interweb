package de.l3s.interweb.core.embeddings;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.l3s.interweb.core.util.StringOrArrayDeserializer;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbeddingsQuery {

    /**
     * ID of the model to use. Use `GET /models` to retrieve available models.
     */
    @NotEmpty
    @JsonProperty("model")
    private String model;

    /**
     * Input text to embed, encoded as a string or array of tokens.
     * To embed multiple inputs in a single request, pass an array of strings or array of token arrays.
     */
    @NotEmpty
    @JsonProperty("input")
    @JsonDeserialize(using = StringOrArrayDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<String> input;

    /**
     * The number of dimensions the resulting output embeddings should have. Only supported in text-embedding-3 and later models.
     */
    @JsonProperty("dimensions")
    private Integer dimensions;

    /**
     * Truncates the end of each input to fit within context length. Returns error if false and context length is exceeded. Defaults to true
     */
    @JsonProperty(value = "truncate")
    private Boolean truncate;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getInput() {
        return input;
    }

    @JsonIgnore
    public void setInput(String input) {
        this.input = List.of(input);
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    public Integer getDimensions() {
        return dimensions;
    }

    public void setDimensions(Integer dimensions) {
        this.dimensions = dimensions;
    }

    public Boolean getTruncate() {
        return truncate;
    }

    public void setTruncate(Boolean truncate) {
        this.truncate = truncate;
    }
}
