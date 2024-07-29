package de.l3s.interweb.connector.ollama.entity;

import de.l3s.interweb.core.chat.CompletionsQuery;

public final class ChatStreamBody extends ChatBody {

    public ChatStreamBody(CompletionsQuery query) {
        super(query);
    }

    @Override
    public Boolean getStream() {
        return true;
    }
}
