package de.l3s.interweb.core.completion;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ResponseFormat {

    public enum ResponseType {
        json_object,
        text
    }

    private ResponseType type;

    public ResponseFormat() {
    }

    public ResponseFormat(ResponseType type) {
        this.type = type;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }
}
