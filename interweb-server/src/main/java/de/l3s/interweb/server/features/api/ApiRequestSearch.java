package de.l3s.interweb.server.features.api;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import de.l3s.interweb.core.search.ContentType;
import de.l3s.interweb.server.features.user.User;

@Entity
@Cacheable
@Table(name = "api_request_search")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiRequestSearch extends PanacheEntityBase {
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
    public String engine;

    @NotNull
    @Column(name = "content_type")
    public String contentType;

    @NotNull
    public String query;

    @NotNull
    @Column(name = "est_cost")
    public Double estimatedCost = 0d;

    @CreationTimestamp
    public Instant created;

    public static ApiRequestSearch of(String engine, ContentType contentType, String query, Double estimatedCost, ApiKey apikey) {
        ApiRequestSearch request = new ApiRequestSearch();
        request.user = apikey.user;
        request.apikey = apikey;
        request.engine = engine;
        request.contentType = contentType.name();
        request.query = query;
        request.estimatedCost = estimatedCost;
        return request;
    }
}
