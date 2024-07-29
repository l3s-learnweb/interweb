package de.l3s.interweb.core.models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "object", "owned_by", "created"})
public class Model {
    private String id;
    private String object = "model";
    @JsonProperty("owned_by")
    private String ownedBy;
    private UsagePrice price;
    @JsonIgnore
    private String provider;
    private Instant created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public UsagePrice getPrice() {
        return price;
    }

    public void setPrice(UsagePrice price) {
        this.price = price;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public static Model of(String id, String ownedBy, UsagePrice price, LocalDate created) {
        Model model = new Model();
        model.setId(id);
        model.setOwnedBy(ownedBy);
        model.setPrice(price);
        if (created != null) {
            model.setCreated(created.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return model;
    }
}
