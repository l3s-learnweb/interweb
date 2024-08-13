package de.l3s.interweb.server.features.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.l3s.interweb.server.features.user.ApiKey;

import de.l3s.interweb.server.features.user.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

public class ChatRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
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
}
