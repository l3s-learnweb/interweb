package de.l3s.interweb.server.chat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import de.l3s.interweb.core.completion.*;
import de.l3s.interweb.core.util.StringUtils;
import de.l3s.interweb.server.principal.Consumer;

@Path("/chat")
public class ChatResource {

    @Inject
    ChatService chatService;

    @Context
    SecurityIdentity securityIdentity;

    @GET
    @Authenticated
    @Path("/models")
    public Map<String, UsagePrice> models() {
        return chatService.getModels();
    }

    @GET
    @Authenticated
    public Uni<List<Chat>> chats(
        @QueryParam("user") String user,
        @QueryParam("sort") @DefaultValue("-created") String order,
        @QueryParam("page") @DefaultValue("1") Integer page,
        @QueryParam("perPage") @DefaultValue("20") Integer perPage
    ) {
        Consumer consumer = securityIdentity.getCredential(Consumer.class);

        return Chat.listByUser(consumer, user, order, page, perPage)
            .flatMap(chats -> Multi.createFrom().iterable(chats).filter(chat -> chat.title == null).call(ChatResource::createChatTitle).collect().asList());
    }

    private static Uni<List<ChatMessage>> createChatTitle(Chat chat) {
        return Mutiny.fetch(chat.getMessages()).call(() -> {
            if (!chat.getMessages().isEmpty()) {
                for (ChatMessage message : chat.getMessages()) {
                    if (message.role == Message.Role.user) {
                        chat.title = StringUtils.shorten(message.content, 120);
                        return chat.updateTitle();
                    }
                }
            }

            return Uni.createFrom().voidItem();
        });
    }

    @GET
    @Authenticated
    @Path("{uuid}")
    public Uni<Conversation> chat(@PathParam("uuid") UUID id) {
        Consumer consumer = securityIdentity.getCredential(Consumer.class);

        return Chat.findById(consumer, id).call(chat -> Mutiny.fetch(chat.getMessages())).map(chat -> {
            Conversation conversation = new Conversation();
            conversation.setId(chat.id);
            conversation.setTitle(chat.title);
            conversation.setUsedTokens(chat.usedTokens);
            conversation.setEstimatedCost(chat.estimatedCost);
            conversation.setCreated(chat.created);
            conversation.setMessages(chat.getMessages().stream().map(ChatMessage::toMessage).toList());
            return conversation;
        });
    }

    @POST
    @Authenticated
    @Path("/completions")
    public Uni<CompletionResults> completions(@Valid CompletionQuery query) {
        Consumer consumer = securityIdentity.getCredential(Consumer.class);

        return getOrCreateChat(query, consumer).flatMap(chat -> {
            // noinspection CodeBlock2Expr
            return chatService.completions(query).call(results -> {
                results.setChatId(chat.id);
                return persistMessages(chat, query.getMessages(), results);
            }).invoke(results -> {
                chat.addCosts(results.getUsage().getTotalTokens(), results.getCost().getResponse());
                results.getCost().setChat(chat.estimatedCost);
            }).call(results -> {
                if (chat.title == null && query.isGenerateTitle()) {
                    return generateTitle(chat).invoke(titleCompletion -> {
                        chat.title = titleCompletion.getLastMessage().getContent();
                        results.setChatTitle(chat.title);

                        chat.addCosts(titleCompletion.getUsage().getTotalTokens(), titleCompletion.getCost().getResponse());
                        results.getCost().setChat(chat.estimatedCost);
                    });
                } else {
                    return Uni.createFrom().voidItem();
                }
            }).call(chat::updateTitleAndUsage);
        });
    }

    private Uni<Chat> getOrCreateChat(CompletionQuery query, Consumer consumer) {
        if (query.getId() != null) {
            return Chat.findById(consumer, query.getId()).call(chat -> Mutiny.fetch(chat.getMessages()));
        }

        Chat chat = new Chat();
        chat.consumer = consumer;
        chat.model = query.getModel();
        chat.user = query.getUser();
        return Panache.withTransaction(chat::persist);
    }

    private Uni<Void> persistMessages(final Chat chat, final List<Message> queryMessages, final CompletionResults results) {
        for (Message message : queryMessages) {
            if (message.getId() == null) {
                ChatMessage cm = new ChatMessage(message);
                if (!chat.getMessages().contains(cm)) {
                    chat.addMessage(cm);
                }
            }
        }

        chat.addMessage(new ChatMessage(results.getLastMessage()));
        return Panache.withTransaction(() -> ChatMessage.persist(chat.getMessages()));
    }

    private Uni<CompletionResults> generateTitle(final Chat chat) {
        CompletionQuery query = new CompletionQuery();
        query.setMessages(chat.getMessages().stream().map(ChatMessage::toMessage).toList());
        query.addMessage("Give a short name for this conversation; don't use any formatting; length between 80 and 120 characters", Message.Role.user);
        return chatService.completions(query);
    }
}
