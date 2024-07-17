package de.l3s.interweb.connector.ollama.entity;

import de.l3s.interweb.core.completion.CompletionQuery;

public final class ChatStreamBody extends ChatBody {

    public ChatStreamBody(CompletionQuery query) {
        super(query);
    }

    public Boolean getStream() {
        return true;
    }
}
