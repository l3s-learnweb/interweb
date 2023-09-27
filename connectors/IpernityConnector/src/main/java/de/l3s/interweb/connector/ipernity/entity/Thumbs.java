package de.l3s.interweb.connector.ipernity.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public record Thumbs(

    @JsonProperty("thumb")
    List<Thumb> thumb
) {
}