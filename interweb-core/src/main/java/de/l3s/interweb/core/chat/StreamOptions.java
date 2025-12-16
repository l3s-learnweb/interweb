package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class StreamOptions {

    /**
     * If set, an additional chunk will be streamed before the data: [DONE] message.
     * The usage field on this chunk shows the token usage statistics for the entire request,
     * and the choices field will always be an empty array.
     * All other chunks will also include a usage field, but with a null value.
     */
    @JsonProperty("include_usage")
    private Boolean includeUsage;

    public StreamOptions() {
    }

    public StreamOptions(Boolean includeUsage) {
        this.includeUsage = includeUsage;
    }

    public Boolean getIncludeUsage() {
        return includeUsage;
    }

    public void setIncludeUsage(Boolean includeUsage) {
        this.includeUsage = includeUsage;
    }
}
