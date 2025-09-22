package de.l3s.interweb.core.chat;

import java.io.Serial;
import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class UrlCitation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1064702155813205467L;

    /**
     * The title of the web resource.
     */
    private String title;

    /**
     * The URL of the web resource.
     */
    private String url;

    /**
     * The index of the first character of the URL citation in the message.
     */
    @JsonProperty("start_index")
    private Integer startIndex;

    /**
     * The index of the last character of the URL citation in the message.
     */
    @JsonProperty("end_index")
    private Integer endIndex;
}
