package de.l3s.interweb.server.features.api;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class RequestsService {

    @WithSession
    @ConsumeEvent("api-request-chat")
    public Uni<Void> consumeChatRequest(ApiChatRequest request) {
        return request.persistAndFlush().replaceWithVoid();
    }

}
