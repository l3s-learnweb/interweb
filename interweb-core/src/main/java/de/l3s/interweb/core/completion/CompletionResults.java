package de.l3s.interweb.core.completion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import de.l3s.interweb.core.Results;

public class CompletionResults extends Results<Choice> {
    private UUID chatId;
    private String model;
    private Usage usage;
    private Instant created;

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choice> getChoices() {
        return getResults();
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

}
