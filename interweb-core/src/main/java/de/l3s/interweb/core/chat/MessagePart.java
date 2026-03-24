package de.l3s.interweb.core.chat;

import java.io.Serial;
import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessagePart implements Serializable {
    @Serial
    private static final long serialVersionUID = 7321150327615238431L;

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE_URL = "image_url";
    public static final String MIME_DATA_IMAGE = "data:image/";

    /**
     * The type of the content part. Can be "text" or "image_url".
     */
    private String type;

    /**
     * The text content.
     */
    private String text;

    /**
     * The image URL.
     */
    @JsonProperty("image_url")
    private ImageUrl imageUrl;

    public MessagePart() {
    }

    public MessagePart(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public MessagePart(String type, ImageUrl imageUrl) {
        this.type = type;
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ImageUrl getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ImageUrl imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static MessagePart text(String text) {
        return new MessagePart("text", text);
    }

    public static MessagePart imageUrl(String url) {
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setUrl(url);
        return new MessagePart("image_url", imageUrl);
    }

    @RegisterForReflection
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ImageUrl implements Serializable {
        @Serial
        private static final long serialVersionUID = -3746742450532357308L;

        private String url;
        /**
         * Specifies the detail level of the image. Can be "low", "high", or "auto".
         */
        private String detail;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}

