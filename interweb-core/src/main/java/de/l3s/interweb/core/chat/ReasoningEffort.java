package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum ReasoningEffort {
    minimal,
    low,
    medium,
    high
}
