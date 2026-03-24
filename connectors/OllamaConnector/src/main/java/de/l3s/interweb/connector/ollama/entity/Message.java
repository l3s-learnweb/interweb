package de.l3s.interweb.connector.ollama.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.l3s.interweb.core.chat.MessagePart;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Message {
    private Role role;
    private String content;
    private String thinking;
    private List<String> images;
    /**
     * We keep it as object, we don't want to care about its internal structure here.
     */
    @JsonProperty("tool_calls")
    private Object toolCalls;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThinking() {
        return thinking;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Object getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(Object toolCalls) {
        this.toolCalls = toolCalls;
    }

    public static Message of(de.l3s.interweb.core.chat.Message message) {
        Message result = new Message();
        result.setRole(Role.of(message.getRole()));
        result.setToolCalls(message.getToolCalls());

        if (message.getContent() instanceof String s) {
            result.setContent(s);
        } else if (message.getContent() instanceof List<?> list) {
            StringBuilder textContent = new StringBuilder();
            List<String> images = new ArrayList<>();
            for (Object obj : list) {
                String type = null;
                String text = null;
                String url = null;

                if (obj instanceof MessagePart part) {
                    type = part.getType();
                    text = part.getText();
                    if (part.getImageUrl() != null) {
                        url = part.getImageUrl().getUrl();
                    }
                } else if (obj instanceof Map<?, ?> map) {
                    Object typeObj = map.get("type");
                    Object textObj = map.get("text");
                    type = typeObj instanceof String ? (String) typeObj : null;
                    text = textObj instanceof String ? (String) textObj : null;
                    Object urlObj = map.get("image_url");
                    if (urlObj instanceof Map<?, ?> urlMap) {
                        Object urlValue = urlMap.get("url");
                        url = urlValue instanceof String ? (String) urlValue : null;
                    }
                }

                if (MessagePart.TYPE_TEXT.equals(type) && text != null) {
                    textContent.append(text);
                } else if (MessagePart.TYPE_IMAGE_URL.equals(type) && url != null && url.startsWith(MessagePart.MIME_DATA_IMAGE)) {
                    int commaIndex = url.indexOf(',');
                    if (commaIndex != -1) {
                        images.add(url.substring(commaIndex + 1));
                    }
                }
            }
            if (!textContent.isEmpty()) {
                result.setContent(textContent.toString());
            }
            if (!images.isEmpty()) {
                result.setImages(images);
            }
        }

        return result;
    }

    public de.l3s.interweb.core.chat.Message toMessage() {
        de.l3s.interweb.core.chat.Message result = new de.l3s.interweb.core.chat.Message();
        result.setRole(this.getRole().toRole());
        result.setContent(this.getContent());
        result.setToolCalls(this.getToolCalls());
        return result;
    }
}
