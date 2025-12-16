package de.l3s.interweb.connector.ollama.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.chat.Tool;

@RegisterForReflection
public final class ChatStreamBody extends ChatBody {

    @Override
    public Boolean getStream() {
        return true;
    }

    @Override
    public List<Tool> getTools() {
        return null;
    }
}
