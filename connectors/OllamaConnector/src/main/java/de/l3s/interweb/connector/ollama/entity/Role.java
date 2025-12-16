package de.l3s.interweb.connector.ollama.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum Role {
    system,
    user,
    assistant,
    tool;

    public de.l3s.interweb.core.chat.Role toRole() {
        return switch (this) {
            case system -> de.l3s.interweb.core.chat.Role.system;
            case user -> de.l3s.interweb.core.chat.Role.user;
            case assistant -> de.l3s.interweb.core.chat.Role.assistant;
            case tool -> de.l3s.interweb.core.chat.Role.tool;
        };
    }

    public static Role of(de.l3s.interweb.core.chat.Role role) {
        return switch (role) {
            case system, developer -> system;
            case user -> user;
            case assistant -> assistant;
            case tool -> tool;
        };
    }
}
