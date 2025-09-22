package de.l3s.interweb.core.chat;

import java.io.Serial;
import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class Annotation implements Serializable {
    @Serial
    private static final long serialVersionUID = -815848835509812621L;

    /**
     * The type of the URL citation. Always url_citation.
     */
    private String type;

    /**
     * A URL citation when using web search.
     */
    @JsonProperty("url_citation")
    private UrlCitation urlCitation;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setUrlCitation(UrlCitation urlCitation) {
        this.urlCitation = urlCitation;
    }

    public UrlCitation getUrlCitation() {
        return urlCitation;
    }
}
