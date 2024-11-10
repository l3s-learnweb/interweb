package de.l3s.interweb.server.features.api;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UsageSummary(ChatUsage chat) {
}
