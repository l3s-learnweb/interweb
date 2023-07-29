package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Owner {
    @JsonProperty("user_id")
    public String userId;
    public String username;
    public String alias;
    @JsonProperty("is_pro")
    public Integer isPro;
    public String icon;
}
