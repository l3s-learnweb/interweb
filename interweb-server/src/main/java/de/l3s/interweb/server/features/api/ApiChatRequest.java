package de.l3s.interweb.server.features.api;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.l3s.interweb.core.chat.CompletionsResults;
import de.l3s.interweb.server.features.user.User;

@Entity
@Cacheable
@Table(name = "api_request_chat")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiChatRequest extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    public User user;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public ApiKey apikey;

    @NotNull
    public String model;

    @NotNull
    @Column(name = "input_tokens")
    public Integer inputTokens = 0;

    @NotNull
    @Column(name = "output_tokens")
    public Integer outputTokens = 0;

    @NotNull
    @Column(name = "est_cost")
    public Double estimatedCost = 0d;

    @CreationTimestamp
    public Instant created;

    public static ApiChatRequest of(CompletionsResults results, ApiKey apikey) {
        ApiChatRequest request = new ApiChatRequest();
        request.user = apikey.user;
        request.apikey = apikey;
        request.model = results.getModel();
        if (results.getUsage() != null) {
            request.inputTokens = results.getUsage().getPromptTokens();
            request.outputTokens = results.getUsage().getCompletionTokens();
        }
        if (results.getCost() != null) {
            request.estimatedCost = results.getCost().getTotal();
        }
        return request;
    }
}
