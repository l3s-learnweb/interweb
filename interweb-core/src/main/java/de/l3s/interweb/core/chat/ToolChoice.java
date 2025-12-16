package de.l3s.interweb.core.chat;

public enum ToolChoice {
    /**
     * Means the model will not call any tool and instead generates a message
     */
    none,
    /**
     * Means the model can pick between generating a message or calling one or more tools.
     */
    auto,
    /**
     * Means the model must call one or more tools.
     */
    required,
}
