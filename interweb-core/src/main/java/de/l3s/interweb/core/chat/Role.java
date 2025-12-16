package de.l3s.interweb.core.chat;

public enum Role {
    /**
     * Developer-provided instructions that the model should follow, regardless of messages sent by the user.
     * With o1 models and newer, use `developer` messages for this purpose instead.
     */
    system,
    /**
     * Developer-provided instructions that the model should follow, regardless of messages sent by the user.
     * With o1 models and newer, `developer` messages replace the previous `system` messages.
     */
    developer,
    /**
     * Messages sent by an end user, containing prompts or additional context information.
     */
    user,
    /**
     * Messages sent by the model in response to user messages.
     */
    assistant,
    /**
     * Messages containing tool outputs.
     */
    tool,
}
