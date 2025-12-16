package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class GenerateStreamBody extends GenerateBody {

    @Override
    public Boolean getStream() {
        return true;
    }
}

