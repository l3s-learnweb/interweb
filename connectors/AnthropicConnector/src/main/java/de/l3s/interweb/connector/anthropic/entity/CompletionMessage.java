package de.l3s.interweb.connector.anthropic.entity;

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

import de.l3s.interweb.core.chat.Message;
import de.l3s.interweb.core.chat.MessagePart;

@RegisterForReflection
public final class CompletionMessage {
    private String role;
    private String content;

    public CompletionMessage(Message message) {
        this.role = message.getRole().name();
        this.content = extractContentString(message.getContent());
    }

    private static String extractContentString(Object contentObj) {
        if (contentObj == null) {
            return null;
        }
        if (contentObj instanceof String s) {
            return s;
        }
        if (contentObj instanceof List<?> list) {
            StringBuilder sb = new StringBuilder();
            for (Object item : list) {
                if (item instanceof MessagePart part) {
                    if (MessagePart.TYPE_TEXT.equals(part.getType()) && part.getText() != null) {
                        sb.append(part.getText());
                    }
                } else if (item instanceof Map<?, ?> map) {
                    Object typeObj = map.get("type");
                    Object textObj = map.get("text");
                    if (MessagePart.TYPE_TEXT.equals(typeObj) && textObj instanceof String text) {
                        sb.append(text);
                    }
                }
            }
            return !sb.isEmpty() ? sb.toString() : null;
        }
        return contentObj.toString();
    }

    public static String extractSystemMessage(Message message) {
        return extractContentString(message.getContent());
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
