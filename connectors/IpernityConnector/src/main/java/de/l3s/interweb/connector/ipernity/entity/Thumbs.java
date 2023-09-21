package de.l3s.interweb.connector.ipernity.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Thumbs(

    @JsonProperty("thumb")
    List<Thumb> thumb
) {
}