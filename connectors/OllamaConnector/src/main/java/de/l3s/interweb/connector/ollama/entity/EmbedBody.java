package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.l3s.interweb.core.embeddings.EmbeddingsQuery;
import de.l3s.interweb.core.util.StringOrArrayDeserializer;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbedBody {
    private String model;
    @JsonDeserialize(using = StringOrArrayDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<String> input;
    private Boolean truncate;
    private Integer dimensions;
    @JsonProperty("keep_alive")
    private String keepAlive;
    private ModelOptions options;

    public String getModel() {
        return model;
    }

    public List<String> getInput() {
        return input;
    }

    public Boolean getTruncate() {
        return truncate;
    }

    public Integer getDimensions() {
        return dimensions;
    }

    public String getKeepAlive() {
        return keepAlive;
    }

    public ModelOptions getOptions() {
        return options;
    }

    public void setOptions(ModelOptions options) {
        this.options = options;
    }

    public static EmbedBody of(EmbeddingsQuery query) {
        EmbedBody body = new EmbedBody();
        body.model = query.getModel();
        body.input = query.getInput();
        body.truncate = query.getTruncate();
        body.dimensions = query.getDimensions();
        // Initialize options with query
        body.options = ModelOptions.of(query);
        return body;
    }
}
