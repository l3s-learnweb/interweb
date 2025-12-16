package de.l3s.interweb.core.responses;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.ResponseFormat;

@RegisterForReflection
public class ResponseText {
    @JsonProperty("format")
    private ResponseFormat format;

    public ResponseFormat getFormat() {
        return format;
    }

    public void setFormat(ResponseFormat format) {
        this.format = format;
    }
}

